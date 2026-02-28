package com.social.employ.service;

import com.social.employ.domain.entity.User;
import com.social.employ.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginTerm) throws UsernameNotFoundException {
        //busca user
        var usuario = userRepository.findByUsernameOrEmail(loginTerm, loginTerm)
                .filter(User::isActive)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado, inactivo o email incorrecto: " + loginTerm));

        //pasó el filtro y cosnstruye el UserDetails de Spring
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getRole().name())
                .build();
    }
}