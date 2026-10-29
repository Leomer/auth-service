package com.bank.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Token returned after a successful login. */
@Data
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private String tokenType;
    private long expiresInMinutes;
}