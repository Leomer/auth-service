package com.bank.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** Login request. */
@Data
public class LoginRequest {
    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;
}