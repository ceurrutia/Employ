package com.social.employ.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(SecurityFilter securityFilter, CustomAccessDeniedHandler accessDeniedHandler) {
        this.securityFilter = securityFilter;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    // 1. RUTAS PÚBLICAS (Sin token)
                    req.requestMatchers(HttpMethod.POST, "/api/auth/users/login", "/api/auth/users/register").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll();
                    req.requestMatchers(HttpMethod.GET, "/api/offers/**").permitAll();

                    // 2. REGLAS DE ADMIN (Específicas y prioritarias)
                    // Usamos hasRole porque tu filtro pone "ROLE_ADMIN"
                    req.requestMatchers(HttpMethod.GET, "/api/auth/users").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/auth/users/active").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/auth/users/inactive").hasRole("ADMIN");
                    req.requestMatchers("/actuator/**").hasRole("ADMIN");

                    // 3. RUTAS DE APLICACIONES
                    req.requestMatchers("/api/applications/**").authenticated();

                    // 4. RUTAS DE USUARIOS GENERALES (Me, perfiles, etc)
                    // IMPORTANTE: Esta va al final de las de auth/users para no pisar las de ADMIN
                    req.requestMatchers("/api/auth/users/**").authenticated();

                    req.anyRequest().authenticated();
                })
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}