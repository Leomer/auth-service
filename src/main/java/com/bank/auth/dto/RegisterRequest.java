package com.bank.auth.dto;

import com.bank.auth.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Registration request. */
@Data
public class RegisterRequest {
    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must have at least 6 characters")
    private String password;

    /** Defaults to USER when omitted. */
    private Role role;
}