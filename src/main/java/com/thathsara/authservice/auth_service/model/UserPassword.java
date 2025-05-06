package com.thathsara.authservice.auth_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "user_passwords")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPassword {
    @Id
    private long id;

    

    @NotBlank(message = "Email is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable= false , unique=true)
    private String password;
}
