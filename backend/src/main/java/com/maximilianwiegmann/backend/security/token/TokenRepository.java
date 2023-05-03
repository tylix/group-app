package com.maximilianwiegmann.backend.security.token;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {

    @Query("{ 'userId' : ?0, 'revoked' : false, 'expired' : false }")
    List<Token> findAllValidTokensByUserId(String userId);

    Optional<Token> findByToken(String token);
}
