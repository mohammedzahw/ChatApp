package com.example.chat.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordRequest {
    @Email(message = "Invalid email address")
    @NotEmpty(message = "Email cannot be empty")
    public String email;
    @NotEmpty(message = "Password cannot be empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$", message = "Password must contain at least one number, one uppercase letter, one lowercase letter, and be at least 8 characters long")

    private String password;
    @NotEmpty(message = "Confirm password cannot be empty")
    private String confirmPassword;

    @AssertTrue(message = "Passwords must match")
    private boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) {
            return false;
        } else {
            return password.equals(confirmPassword);
        }
    }

}
