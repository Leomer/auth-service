package com.bank.auth.service;

import com.bank.auth.dto.LoginRequest;
import com.bank.auth.dto.RegisterRequest;
import com.bank.auth.dto.TokenResponse;
import io.reactivex.rxjava3.core.Single;

/** Registration and login. */
public interface AuthService {

    Single<String> register(RegisterRequest request);

    Single<TokenResponse> login(LoginRequest request);
}