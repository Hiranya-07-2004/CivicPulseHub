package com.CivicPulse.CivicPulseHub.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$",
            message = "Email must end with @gmail.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "Password must be 8+ chars, include upper, lower, digit & special character"
    )
    private String password;

    @NotBlank(message = "Government ID is required")
    @Pattern(
            regexp = "^(citizen).+",
            message = "Government ID must start with citizen "
    )
    private String governmentId;
}