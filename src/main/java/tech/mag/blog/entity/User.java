
package tech.mag.blog.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username")
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters")
    private String username;

    @Column(name = "email")
    @NotBlank(message = "email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "password")
    @Size(min = 5, max = 50, message = "Password must be between 5 and 50 characters")
    @JsonIgnore
    private String password;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "user_role", columnDefinition = "varchar(255) default 'AUTHOR'")
    @Enumerated(EnumType.STRING)
    private Role role = Role.AUTHOR;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}