package com.social.employ.config;

import com.social.employ.domain.entity.User;
import com.social.employ.repository.UserRepository;
import com.social.employ.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var token = authHeader.replace("Bearer ", "");
            try {
                var nombreUsuario = tokenService.getSubject(token);

                if (nombreUsuario != null) {
                    var usuarioOptional = userRepository.findByUsernameOrEmail(nombreUsuario, nombreUsuario);

                    if (usuarioOptional.isPresent()) {
                        User usuario = usuarioOptional.get();

                        //prefijo ROLE_ que Spring espera
                        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()));

                        var authentication = new UsernamePasswordAuthenticationToken(
                                usuario.getUsername(),
                                null,
                                authorities
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error en la validación del token: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}