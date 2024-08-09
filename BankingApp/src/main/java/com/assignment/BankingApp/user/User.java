package com.assignment.BankingApp.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "users")
public class User {
    private static final int MIN_PASSWORD_LENGTH = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username;

    @Size(min = MIN_PASSWORD_LENGTH, message = "Password must be at least " + MIN_PASSWORD_LENGTH + " characters")
    private String password;

    private String role;

    @Email(message = "Email should be valid")
    private String email;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    private String address;

    private Long balance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.password = user.password;
        this.role = user.role;
        this.email = user.email;
        this.address = user.address;
        this.isActive = user.isActive;
        this.createdAt = user.createdAt;
        this.updatedAt = user.updatedAt;
    }

    public User() { }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
