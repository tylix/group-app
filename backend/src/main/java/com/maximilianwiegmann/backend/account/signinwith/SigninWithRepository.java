package com.maximilianwiegmann.backend.account.signinwith;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SigninWithRepository extends MongoRepository<SigninWith, String> {

    Optional<SigninWith> findByProviderAndProviderId(String provider, String providerId);

}

