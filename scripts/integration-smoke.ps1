param(
    [Parameter(Mandatory = $true)]
    [string]$UsersJar,
    [Parameter(Mandatory = $true)]
    [string]$RestaurantsJar
)

$ErrorActionPreference = "Stop"
$UsersJar = (Resolve-Path $UsersJar).Path
$RestaurantsJar = (Resolve-Path $RestaurantsJar).Path
$java = (Get-Command java -ErrorAction Stop).Source
$logFiles = @(
    [IO.Path]::GetTempFileName(),
    [IO.Path]::GetTempFileName(),
    [IO.Path]::GetTempFileName(),
    [IO.Path]::GetTempFileName()
)
$environmentNames = @(
    "JWT_SECRET",
    "SERVER_PORT",
    "SPRING_DATASOURCE_URL",
    "SPRING_DATASOURCE_DRIVER",
    "SPRING_DATASOURCE_USERNAME",
    "SPRING_DATASOURCE_PASSWORD",
    "SPRING_H2_CONSOLE_ENABLED",
    "H2_CONSOLE_ENABLED",
    "SPRING_JPA_SHOW_SQL",
    "SPRING_JPA_HIBERNATE_DDL_AUTO",
    "SPRING_JPA_DDL_AUTO",
    "BOOTSTRAP_ADMIN_ENABLED",
    "BOOTSTRAP_ADMIN_EMAIL",
    "BOOTSTRAP_ADMIN_PASSWORD",
    "BOOTSTRAP_ADMIN_NAME",
    "BOOTSTRAP_ADMIN_LASTNAME",
    "BOOTSTRAP_ADMIN_DOCUMENT",
    "BOOTSTRAP_ADMIN_PHONE",
    "BOOTSTRAP_ADMIN_BIRTHDATE",
    "RESTAURANTES_URL",
    "USERS_ROLE_URL_TEMPLATE"
)
$previousEnvironment = @{}
foreach ($name in $environmentNames) {
    $previousEnvironment[$name] = [Environment]::GetEnvironmentVariable($name, "Process")
}
$usersProcess = $null
$restaurantsProcess = $null

function Assert-Condition {
    param([bool]$Condition, [string]$Message)
    if (-not $Condition) {
        throw $Message
    }
}

function Invoke-JsonRequest {
    param(
        [string]$Method,
        [string]$Uri,
        [string]$Token,
        [object]$Body
    )
    $request = @{
        Method = $Method
        Uri = $Uri
        TimeoutSec = 15
        ErrorAction = "Stop"
    }
    if ($Token) {
        $request.Headers = @{ Authorization = "Bearer $Token" }
    }
    if ($null -ne $Body) {
        $request.ContentType = "application/json"
        $request.Body = ConvertTo-Json -InputObject $Body -Depth 8 -Compress
    }
    Invoke-RestMethod @request
}

function Get-JwtSubject {
    param([string]$Token)
    $segment = $Token.Split('.')[1].Replace('-', '+').Replace('_', '/')
    switch ($segment.Length % 4) {
        2 { $segment += '==' }
        3 { $segment += '=' }
    }
    $claims = [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($segment)) |
        ConvertFrom-Json
    [string]$claims.sub
}

function Wait-ForService {
    param(
        [System.Diagnostics.Process]$Process,
        [string]$Uri,
        [string]$Name,
        [string[]]$Logs
    )
    $deadline = [DateTime]::UtcNow.AddSeconds(90)
    while ([DateTime]::UtcNow -lt $deadline) {
        if ($Process.HasExited) {
            throw "$Name exited during startup. Logs: $($Logs -join ', ')"
        }
        try {
            Invoke-RestMethod -Uri $Uri -TimeoutSec 2 -ErrorAction Stop | Out-Null
            return
        }
        catch {
            Start-Sleep -Seconds 1
        }
    }
    throw "$Name did not become ready. Logs: $($Logs -join ', ')"
}

try {
    $randomBytes = New-Object byte[] 48
    [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($randomBytes)
    $env:JWT_SECRET = [Convert]::ToBase64String($randomBytes)
    $env:SPRING_DATASOURCE_DRIVER = "org.h2.Driver"
    $env:SPRING_DATASOURCE_USERNAME = "sa"
    $env:SPRING_DATASOURCE_PASSWORD = ""
    $env:SPRING_JPA_SHOW_SQL = "false"
    $env:SPRING_JPA_HIBERNATE_DDL_AUTO = "validate"
    $env:BOOTSTRAP_ADMIN_ENABLED = "true"
    $env:BOOTSTRAP_ADMIN_EMAIL = "admin@integration.test"
    $env:BOOTSTRAP_ADMIN_PASSWORD = "IntegrationAdmin123!"
    $env:BOOTSTRAP_ADMIN_NAME = "Integration"
    $env:BOOTSTRAP_ADMIN_LASTNAME = "Admin"
    $env:BOOTSTRAP_ADMIN_DOCUMENT = "900000001"
    $env:BOOTSTRAP_ADMIN_PHONE = "3001234567"
    $env:BOOTSTRAP_ADMIN_BIRTHDATE = "1980-01-01"
    $env:SERVER_PORT = "18081"
    $env:SPRING_DATASOURCE_URL = "jdbc:h2:mem:integration_users;DB_CLOSE_DELAY=-1"
    $env:SPRING_H2_CONSOLE_ENABLED = "false"
    $env:RESTAURANTES_URL = "http://localhost:18082"

    $usersProcess = Start-Process -FilePath $java `
        -ArgumentList ('-jar "' + $UsersJar + '"') -PassThru `
        -RedirectStandardOutput $logFiles[0] -RedirectStandardError $logFiles[1]
    Wait-ForService -Process $usersProcess -Uri "http://localhost:18081/api-docs" `
        -Name "User service" -Logs $logFiles[0..1]

    $env:BOOTSTRAP_ADMIN_ENABLED = "false"
    $env:SERVER_PORT = "18082"
    $env:SPRING_DATASOURCE_URL = "jdbc:h2:mem:integration_restaurants;MODE=MySQL;DB_CLOSE_DELAY=-1"
    $env:USERS_ROLE_URL_TEMPLATE = "http://localhost:18081/internal/usuarios/{usuarioId}/roles/{rol}"
    $restaurantsProcess = Start-Process -FilePath $java `
        -ArgumentList ('-jar "' + $RestaurantsJar + '"') -PassThru `
        -RedirectStandardOutput $logFiles[2] -RedirectStandardError $logFiles[3]
    Wait-ForService -Process $restaurantsProcess -Uri "http://localhost:18082/v3/api-docs" `
        -Name "Restaurant service" -Logs $logFiles[2..3]

    $admin = Invoke-JsonRequest -Method Post -Uri "http://localhost:18081/auth/login" `
        -Body @{ correo = $env:BOOTSTRAP_ADMIN_EMAIL; clave = $env:BOOTSTRAP_ADMIN_PASSWORD }
    $ownerBody = @{
        nombre = "Integration"
        apellido = "Owner"
        documentoIdentidad = "900000002"
        celular = "+573001234568"
        fechaNacimiento = "1990-01-01"
        correo = "owner@integration.test"
        clave = "IntegrationOwner123!"
    }
    Invoke-JsonRequest -Method Post -Uri "http://localhost:18081/usuarios/propietario" `
        -Token $admin.accessToken -Body $ownerBody | Out-Null

    $owner = Invoke-JsonRequest -Method Post -Uri "http://localhost:18081/auth/login" `
        -Body @{ correo = $ownerBody.correo; clave = $ownerBody.clave }
    $ownerId = Get-JwtSubject -Token $owner.accessToken
    $restaurant = Invoke-JsonRequest -Method Post -Uri "http://localhost:18082/api/v1/restaurants" `
        -Token $admin.accessToken -Body @{
            name = "Integration Cafe"
            nit = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds().ToString()
            address = "1 Test Street"
            phone = "+573001234567"
            logoUrl = "https://example.test/logo.png"
            ownerId = $ownerId
        }
    Assert-Condition ($restaurant.id -gt 0) "HU02 restaurant creation failed."

    $employeeBody = @{
        nombre = "Integration"
        apellido = "Employee"
        documentoIdentidad = "900000003"
        celular = "3001234569"
        correo = "employee@integration.test"
        clave = "IntegrationEmployee123!"
        restauranteId = $restaurant.id
    }
    Invoke-JsonRequest -Method Post -Uri "http://localhost:18081/usuarios/empleado" `
        -Token $owner.accessToken -Body $employeeBody | Out-Null

    Invoke-JsonRequest -Method Post -Uri "http://localhost:18081/usuarios/cliente" -Body @{
        nombre = "Integration"
        apellido = "Client"
        documentoIdentidad = "900000004"
        celular = "3001234570"
        correo = "client@integration.test"
        clave = "IntegrationClient123!"
    } | Out-Null
    $client = Invoke-JsonRequest -Method Post -Uri "http://localhost:18081/auth/login" `
        -Body @{ correo = "client@integration.test"; clave = "IntegrationClient123!" }
    $restaurants = Invoke-JsonRequest -Method Get `
        -Uri "http://localhost:18082/api/v1/restaurants?page=0&size=10" -Token $client.accessToken
    $listedRestaurant = @($restaurants.content | Where-Object { $_.name -eq "Integration Cafe" })
    Assert-Condition ($listedRestaurant.Count -eq 1) "HU09 client listing did not return the created restaurant."
    $publicFields = @($listedRestaurant[0].PSObject.Properties.Name | Sort-Object)
    Assert-Condition (($publicFields -join ",") -eq "logoUrl,name") "HU09 returned unexpected restaurant fields."

    $dish = Invoke-JsonRequest -Method Post `
        -Uri "http://localhost:18082/api/v1/restaurants/$($restaurant.id)/dishes" `
        -Token $owner.accessToken -Body @{
            name = "Integration Dish"
            price = 12000
            description = "Original description"
            imageUrl = "https://example.test/dish.png"
            category = "Main"
        }
    Assert-Condition ($dish.active -eq $true) "HU03 dish should be active by default."
    $updatedDish = Invoke-JsonRequest -Method Patch `
        -Uri "http://localhost:18082/api/v1/dishes/$($dish.id)" `
        -Token $owner.accessToken -Body @{ price = 13000; description = "Updated description" }
    Assert-Condition ($updatedDish.price -eq 13000 -and $updatedDish.name -eq "Integration Dish") `
        "HU04 did not update only the allowed dish fields."
    $disabledDish = Invoke-JsonRequest -Method Patch `
        -Uri "http://localhost:18082/api/v1/dishes/$($dish.id)/status" `
        -Token $owner.accessToken -Body @{ active = $false }
    Assert-Condition ($disabledDish.active -eq $false) "HU07 dish status update failed."

    Write-Host "Integration smoke test passed: HU01-HU09 service flows and shared JWT authorization."
}
catch {
    foreach ($index in 0..3) {
        if (Test-Path $logFiles[$index]) {
            Write-Host "---- service log $($index + 1) ----"
            Get-Content $logFiles[$index] -Tail 80
        }
    }
    throw
}
finally {
    foreach ($process in @($restaurantsProcess, $usersProcess)) {
        if ($null -ne $process -and -not $process.HasExited) {
            Stop-Process -Id $process.Id -Force
            $process.WaitForExit()
        }
    }
    foreach ($name in $environmentNames) {
        [Environment]::SetEnvironmentVariable($name, $previousEnvironment[$name], "Process")
    }
    foreach ($file in $logFiles) {
        if (Test-Path $file) {
            Remove-Item -LiteralPath $file -Force
        }
    }
}
