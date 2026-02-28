package com.social.employ;

import com.social.employ.domain.enums.Role;
import com.social.employ.domain.record.UserRegistrationRequest;
import com.social.employ.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserIntegrationTest extends TestBase {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Debería lanzar excepción si el nombre de usuario ya existe")
    void registerUserDuplicateUsernameTest() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "ceciurrutia",
                "ceciliaurrutia@fashion.com",
                "Password123!",
                Role.USER,
                "Bio de prueba",
                null,
                null
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(request);
        });

        assertEquals("El nombre de usuario ya está en uso", exception.getMessage());
    }

    @Test
    @DisplayName("Verificar que el servicio se carga correctamente")
    void contextLoads() {
        assertNotNull(userService);
    }
}