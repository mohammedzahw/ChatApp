package com.example.chat.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {
    @Email(message = "Invalid email address")
    @NotEmpty(message = "Email cannot be empty")
    public String email;
    public String password;
}
