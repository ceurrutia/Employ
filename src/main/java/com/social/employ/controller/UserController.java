package com.social.employ.controller;

import com.social.employ.domain.entity.User;
import com.social.employ.domain.record.*;
import com.social.employ.repository.UserRepository;
import com.social.employ.service.CustomUserDetailsService;
import com.social.employ.service.TokenService;
import com.social.employ.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/users")
public class UserController {

    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public UserController(UserService userService,
                          CustomUserDetailsService customUserDetailsService,
                          PasswordEncoder passwordEncoder,
                          TokenService tokenService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    //registro
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid UserRegistrationRequest request) {
        User createdUser = userService.registerUser(request);
        return new ResponseEntity<>(new UserResponseDTO(createdUser), HttpStatus.CREATED);
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {

        String identifier = (request.username() != null && !request.username().isBlank())
                ? request.username()
                : request.email();

        if (identifier == null || identifier.isBlank()) {
            return ResponseEntity.badRequest().body("Debes proporcionar un username o un email");
        }

        try {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(identifier);

            if (passwordEncoder.matches(request.password(), userDetails.getPassword())) {
                //la entidad User para extraer ID y generar Token
                var usuario = userRepository.findByUsernameOrEmail(identifier, identifier)
                        .orElseThrow();

                String tokenJWT = tokenService.generarToken(usuario);

                return ResponseEntity.ok(new JWTTokenData(tokenJWT));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }
    }

    //me
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyData() {
       //usuario autenticado desde el SecurityContext
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    //get por usuario
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        validarPropiedadOAdmin(id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        //es ADMIN
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        //no es admin ,usuario está inactivo, lanza error
        if (!isAdmin && !user.isActive()) {
            throw new RuntimeException("Usuario no encontrado o inactivo");
        }

        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    //todos los usuarios
    @GetMapping
    //usuarios ADMIN unicamente
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> listAllUsers() {
        var users = userRepository.findAll().stream()
                .map(UserResponseDTO::new)
                .toList();
        return ResponseEntity.ok(users);
    }

    //actualizar
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @RequestBody UserUpdateData data) {
        validarPropiedadOAdmin(id);

        User updatedUser = userService.updateUser(id, data.bio(), data.profilePictureUrl());
        return ResponseEntity.ok(new UserResponseDTO(updatedUser));
    }

    //soft delete usuarios
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        validarPropiedadOAdmin(id);

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    //************ activo e inactivos ***********//

    //ACTIVOS (Solo ADMIN)
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> listActiveUsers() {
        var users = userRepository.findAllByActiveTrue().stream()
                .map(UserResponseDTO::new)
                .toList();
        return ResponseEntity.ok(users);
    }

    //INACTIVOS (Solo ADMIN)
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> listInactiveUsers() {
        var users = userRepository.findAllByActiveFalse().stream()
                .map(UserResponseDTO::new)
                .toList();
        return ResponseEntity.ok(users);
    }


    //valida propiedad
    private void validarPropiedadOAdmin(Long idDestino) {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String usernameLogueado = (String) auth.getPrincipal();

        User usuarioEjecutor = userRepository.findByUsernameOrEmail(usernameLogueado, usernameLogueado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        if (!usuarioEjecutor.getId().equals(idDestino)) {
            throw new org.springframework.security.access.AccessDeniedException("Acceso denegado");
        }
    }
}
