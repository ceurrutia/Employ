package com.social.employ.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.social.employ.domain.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String profilePictureUrl;

    private String bio;

    private String cvUrl;
    private String companyName;

    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor vacío
    public User() {}

    // Constructor completo actualizado
    public User(String username, String email, String password, Role role, String profilePictureUrl, String bio, String cvUrl, String companyName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.profilePictureUrl = profilePictureUrl;
        this.bio = bio;
        this.cvUrl = cvUrl;
        this.companyName = companyName;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public String getBio() { return bio; }
    public String getCvUrl() { return cvUrl; }
    public String getCompanyName() { return companyName; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setBio(String bio) { this.bio = bio; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public void setRole(Role role) { this.role = role; }
    public void setCvUrl(String cvUrl) { this.cvUrl = cvUrl; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setActive(boolean active) { this.active = active; }
}