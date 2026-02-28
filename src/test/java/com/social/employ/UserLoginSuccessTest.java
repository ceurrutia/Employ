package com.social.employ;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.employ.domain.record.JWTTokenData;
import com.social.employ.domain.record.UserLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserLoginSuccessTest extends TestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debería devolver 200 OK y un token JWT válido al loguear correctamente")
    void loginSuccessTest() throws Exception {
        UserLoginRequest request = new UserLoginRequest(
                "ceciurrutia",
                "ceciliaurrutia@fashion.com",
                "Password123!"
        );

        mockMvc.perform(post("/api/auth/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists()) //campo token existe
                .andExpect(jsonPath("$.token").isString()); //es string
    }

    //testea que pueda ver sus datos en /me
    @Test
    @DisplayName("Debería obtener los datos del usuario autenticado usando el token")
    void getMeWithTokenTest() throws Exception {

        UserLoginRequest loginRequest = new UserLoginRequest(
                "ceciurrutia",
                "ceciliaurrutia@fashion.com",
                "Password123!"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        //pasa token a la respuesta
        String responseBody = result.getResponse().getContentAsString();
        JWTTokenData tokenData = objectMapper.readValue(responseBody, JWTTokenData.class);
        String token = tokenData.token();

        //accede a /me usando token ya generado
        mockMvc.perform(get("/api/auth/users/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ceciurrutia"))
                .andExpect(jsonPath("$.email").value("ceciliaurrutia@fashion.com"));
    }
}
