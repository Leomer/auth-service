package com.bank.auth.controller;

import com.bank.auth.dto.LoginRequest;
import com.bank.auth.dto.RegisterRequest;
import com.bank.auth.dto.TokenResponse;
import com.bank.auth.service.AuthService;
import io.reactivex.rxjava3.core.Single;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Public endpoints to register and log in. */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Single<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request).map(u -> Map.of("username", u));
    }

    @PostMapping("/login")
    public Single<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}