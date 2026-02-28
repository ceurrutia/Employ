package com.social.employ.repository;

import com.social.employ.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //login y autenticación
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    //validaciones rápidas en el registro(true si ya existe)
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    List<User> findAllByActiveTrue();
    List<User> findAllByActiveFalse();
}
