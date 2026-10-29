package com.bank.auth.service.impl;

import com.bank.auth.dto.LoginRequest;
import com.bank.auth.dto.RegisterRequest;
import com.bank.auth.exception.BusinessException;
import com.bank.auth.exception.UnauthorizedException;
import com.bank.auth.model.AppUser;
import com.bank.auth.model.Role;
import com.bank.auth.repository.UserRepository;
import com.bank.auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests for {@link AuthServiceImpl}. */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository repository;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtService jwtService =
            new JwtService("test-secret-key-with-at-least-32-characters!!", 60);

    private AuthServiceImpl service() {
        return new AuthServiceImpl(repository, encoder, jwtService);
    }

    private RegisterRequest register(String user) {
        RegisterRequest r = new RegisterRequest();
        r.setUsername(user);
        r.setPassword("secret1");
        return r;
    }

    private LoginRequest login(String user, String pass) {
        LoginRequest l = new LoginRequest();
        l.setUsername(user);
        l.setPassword(pass);
        return l;
    }

    @Test
    void registerShouldStoreHashedPasswordAndDefaultRole() {
        when(repository.existsByUsername("ana")).thenReturn(Mono.just(false));
        when(repository.save(any(AppUser.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        service().register(register("ana")).test().assertValue("ana").assertComplete();

        verify(repository).save(argThat(u ->
                u.getRole() == Role.USER && !"secret1".equals(u.getPasswordHash())));
    }

    @Test
    void registerShouldFailWhenUsernameExists() {
        when(repository.existsByUsername("ana")).thenReturn(Mono.just(true));

        service().register(register("ana")).test().assertError(BusinessException.class);

        verify(repository, never()).save(any());
    }

    @Test
    void loginShouldReturnTokenWithValidCredentials() {
        AppUser user = AppUser.builder().username("ana").role(Role.ADMIN)
                .passwordHash(encoder.encode("secret1")).build();
        when(repository.findByUsername("ana")).thenReturn(Mono.just(user));

        service().login(login("ana", "secret1"))
                .test()
                .assertValue(t -> t.getToken() != null && "Bearer".equals(t.getTokenType()))
                .assertComplete();
    }

    @Test
    void loginShouldFailWithWrongPassword() {
        AppUser user = AppUser.builder().username("ana").role(Role.USER)
                .passwordHash(encoder.encode("secret1")).build();
        when(repository.findByUsername("ana")).thenReturn(Mono.just(user));

        service().login(login("ana", "wrong")).test().assertError(UnauthorizedException.class);
    }

    @Test
    void loginShouldFailWhenUserDoesNotExist() {
        when(repository.findByUsername("nobody")).thenReturn(Mono.empty());

        service().login(login("nobody", "x")).test().assertError(UnauthorizedException.class);
    }
}