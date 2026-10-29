package com.bank.auth.service.impl;

import com.bank.auth.dto.LoginRequest;
import com.bank.auth.dto.RegisterRequest;
import com.bank.auth.dto.TokenResponse;
import com.bank.auth.exception.BusinessException;
import com.bank.auth.exception.UnauthorizedException;
import com.bank.auth.model.AppUser;
import com.bank.auth.model.Role;
import com.bank.auth.repository.UserRepository;
import com.bank.auth.service.AuthService;
import com.bank.auth.service.JwtService;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Default implementation of {@link AuthService}. */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public Single<String> register(RegisterRequest request) {
        log.info("Registering user {}", request.getUsername());
        return Single.fromPublisher(repository.existsByUsername(request.getUsername()))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Single.<String>error(new BusinessException("Username already exists"));
                    }
                    AppUser user = AppUser.builder()
                            .username(request.getUsername())
                            .passwordHash(passwordEncoder.encode(request.getPassword()))
                            .role(request.getRole() == null ? Role.USER : request.getRole())
                            .build();
                    return Single.fromPublisher(repository.save(user)).map(AppUser::getUsername);
                });
    }

    @Override
    public Single<TokenResponse> login(LoginRequest request) {
        log.info("Login attempt for {}", request.getUsername());
        return Maybe.fromPublisher(repository.findByUsername(request.getUsername()))
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPasswordHash()))
                .switchIfEmpty(Single.error(new UnauthorizedException("Invalid credentials")))
                .map(u -> new TokenResponse(jwtService.generate(u), "Bearer",
                        jwtService.getExpirationMinutes()));
    }
}