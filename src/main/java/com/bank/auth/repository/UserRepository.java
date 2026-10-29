package com.bank.auth.repository;

import com.bank.auth.model.AppUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/** Reactive repository for users (derived queries only). */
public interface UserRepository extends ReactiveMongoRepository<AppUser, String> {

    Mono<AppUser> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);
}