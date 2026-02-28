package com.social.employ;

import com.social.employ.controller.UserController;
import com.social.employ.domain.entity.User;
import com.social.employ.domain.record.JWTTokenData;
import com.social.employ.domain.record.UserLoginRequest;
import com.social.employ.repository.UserRepository;
import com.social.employ.service.CustomUserDetailsService;
import com.social.employ.service.TokenService;
import com.social.employ.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserLoginUnitTest {

    @Mock
    private UserService userService;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Prueba Unitaria: Login exitoso")
    void loginSuccessTest() {
        //given de los datos
        UserLoginRequest request = new UserLoginRequest("ceciurrutia", "ceciliaurrutia@fashion.com", "Password123!");

        UserDetails mockUserDetails = mock(UserDetails.class);
        User mockUser = new User();
        mockUser.setUsername("ceciurrutia");

        // comportamiento de los Mocks
        when(customUserDetailsService.loadUserByUsername(anyString())).thenReturn(mockUserDetails);
        when(mockUserDetails.getPassword()).thenReturn("encoded_pass");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(mockUser));
        when(tokenService.generarToken(any(User.class))).thenReturn("fake-jwt-token");

        //when
        ResponseEntity<?> response = userController.login(request);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        //cast manual para verificar el contenido del record
        JWTTokenData data = (JWTTokenData) response.getBody();
        assertEquals("fake-jwt-token", data.token());
    }
}