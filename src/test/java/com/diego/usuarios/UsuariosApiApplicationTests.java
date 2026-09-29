package com.diego.usuarios;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UsuariosApplication.class, properties = {
		"app.bootstrap-admin.enabled=true",
		"app.bootstrap-admin.email=admin@test.local",
		"app.bootstrap-admin.password=AdminPassword123",
		"app.bootstrap-admin.name=Test",
		"app.bootstrap-admin.lastname=Admin",
		"app.bootstrap-admin.document=100000000",
		"app.bootstrap-admin.phone=3001234567",
		"app.bootstrap-admin.birthdate=1980-01-01"
})
@AutoConfigureMockMvc
class UsuariosApiApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void propietarioEndpointRequiresAdministratorToken() throws Exception {
		mockMvc.perform(post("/usuarios/propietario")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void clientRegistrationValidatesRequiredFields() throws Exception {
		mockMvc.perform(post("/usuarios/cliente")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void administratorCanLoginAndCreateAnOwner() throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"correo\":\"admin@test.local\",\"clave\":\"AdminPassword123\"}"))
				.andExpect(status().isOk())
				.andReturn();

		String response = loginResult.getResponse().getContentAsString();
		String token = new com.fasterxml.jackson.databind.ObjectMapper()
				.readTree(response).get("accessToken").asText();
		assertNotNull(token);

		mockMvc.perform(post("/usuarios/propietario")
						.header("Authorization", "Bearer " + token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nombre":"Diego",
								  "apellido":"Prueba",
								  "documentoIdentidad":"123456789",
								  "celular":"+573001234567",
								  "fechaNacimiento":"1990-01-01",
								  "correo":"propietario@test.local",
								  "clave":"Password123"
								}
								"""))
				.andExpect(status().isCreated());
	}
}