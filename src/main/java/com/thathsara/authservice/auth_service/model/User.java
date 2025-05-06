package com.thathsara.authservice.auth_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "invalid tenant id")
    @Column(nullable=false)
    private Long tenantID;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required" )
    @Column(nullable=false, unique=true)
    private String email;

    @NotBlank(message = "Email is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable= false , unique=true)
    private String password;

    @Column(nullable = false , columnDefinition= "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private boolean isVerified= false;

    public enum STATUS {
        ACTIVE, PENDING, BANNED
    }

    @Column(nullable= false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private STATUS status = STATUS.PENDING;

    @CreationTimestamp
    @Column(nullable= false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable= false)
    private LocalDateTime  updatedAt;
}
