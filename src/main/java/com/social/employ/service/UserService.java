package com.social.employ.service;

import com.social.employ.domain.entity.User;
import com.social.employ.domain.enums.Role;
import com.social.employ.domain.record.UserRegistrationRequest;
import com.social.employ.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        //valida si existe mail
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("El email ya está registrado");
        }

        //crea entidad con pass con hash
        User newUser = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.role() != null ? request.role() : Role.USER,
                null,
                request.bio(),
                request.cvUrl(),
                request.companyName()
        );

        return userRepository.save(newUser);
    }

    //update
    @Transactional
    public User updateUser(Long id, String bio, String profilePictureUrl) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (bio != null) user.setBio(bio);
        if (profilePictureUrl != null) user.setProfilePictureUrl(profilePictureUrl);

        return user;
    }

    //soft delete desactiva
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setActive(false);
    }
}