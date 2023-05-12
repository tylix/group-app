package com.maximilianwiegmann.backend.authentication;

import com.maximilianwiegmann.backend.Request;
import com.maximilianwiegmann.backend.account.AccountData;
import com.maximilianwiegmann.backend.authentication.twofactor.TwoFactorService;
import com.maximilianwiegmann.backend.payload.request.AuthenticationRequest;
import com.maximilianwiegmann.backend.payload.request.RegisterRequest;
import com.maximilianwiegmann.backend.payload.request.TwoFARequest;
import com.maximilianwiegmann.backend.payload.response.AuthenticationResponse;
import com.maximilianwiegmann.backend.account.AccountRepository;
import com.maximilianwiegmann.backend.security.config.JwtService;
import com.maximilianwiegmann.backend.security.token.Token;
import com.maximilianwiegmann.backend.security.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final AccountRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TwoFactorService twoFactorService;

    private final Map<String, AuthenticationRequest> authenticationRequests = new HashMap<>();
    private final Map<String, AuthStatus> permitted = new HashMap<>();

    @Value("${github.secret}")
    private String gitHubSecret;

    @Value("${github.clientId}")
    private String gitHubClientId;

    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.findByUsername(request.getUsername()).isPresent()
                || repository.findByEmail(request.getEmail()).isPresent())
            return null;

        var account = AccountData.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .build();
        var savedUser = repository.save(account);
        var jwtToken = jwtService.generateRegisterToken(account);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));
        var account = repository.findByUsername(request.getUsername())
                .orElseThrow();

        AuthStatus authStatus = AuthStatus.builder().build();

        authenticationRequests.put(account.getId(), request);

        String token;
        if (account.getTfaSecret() != null) {
            token = jwtService.generateTfaToken(account);

            authStatus.setOtp(true);
            authStatus.setStatus(AuthStatus.Status.WAIT);
            authStatus.setToken(token);
            authStatus.setUId(account.getId());
            permitted.put(token, authStatus);
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        }
        token = jwtService.generateToken(account);
        revokeAllUserTokens(account);
        saveUserToken(account, token);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public ResponseEntity<?> otp(TwoFARequest request) {
        String token = request.getToken();
        if (token == null)
            return ResponseEntity.badRequest().build();

        var authStatus = permitted.get(token);
        if (authStatus == null)
            return ResponseEntity.badRequest().build();

        if (!authStatus.isOtp())
            return ResponseEntity.badRequest().build();
        if (!authStatus.getStatus().equals(AuthStatus.Status.WAIT))
            return ResponseEntity.badRequest().build();

        if (jwtService.isTokenExpired(token))
            return ResponseEntity.badRequest().build();
        if (!jwtService.isTfaToken(token))
            return ResponseEntity.badRequest().build();

        var account = repository.findById(authStatus.getUId())
                .orElseThrow();

        String otpSecret = account.getTfaSecret();
        if (otpSecret == null)
            return ResponseEntity.badRequest().build();
        if (request.getCode().equals(twoFactorService.getTOTPCode(otpSecret))) {
            if (!authenticationRequests.containsKey(authStatus.getUId()))
                return ResponseEntity.badRequest().build();
            permitted.get(request.getToken()).setStatus(AuthStatus.Status.OK);
            return ResponseEntity.ok("{\"status\": \"OK\"}");
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<?> status(String token) {
        var authStatus = permitted.get(token);
        switch (authStatus.getStatus()) {
            case OK -> {
                AuthenticationRequest request = authenticationRequests.get(authStatus.getUId());
                authenticationRequests.remove(authStatus.getUId());
                permitted.remove(token);

                var account = repository.findByUsername(request.getUsername())
                        .orElseThrow();
                var jwtToken = jwtService.generateToken(account);
                revokeAllUserTokens(account);
                saveUserToken(account, jwtToken);
                return ResponseEntity.ok(AuthenticationResponse.builder()
                        .token(jwtToken)
                        .build());
            }
            case WAIT -> {
                return ResponseEntity.ok("{\"status\": \"WAIT\"}");
            }
            default -> {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    public ResponseEntity<?> signInWith(String provider, String code) {
        switch (provider) {
            case "github" -> {
                JSONObject body = new JSONObject();
                body.put("client_id", this.gitHubClientId);
                body.put("client_secret", this.gitHubSecret);
                body.put("code", code);

                try {
                    JSONObject response = new JSONObject(
                            Request.builder().url("https://github.com/login/oauth/access_token").body(body.toString())
                                    .build().sendRequest());

                    if (response.has("error"))
                        return ResponseEntity.badRequest().build();

                    String accessToken = response.getString("access_token");
                    JSONObject user = new JSONObject(Request.builder()
                            .url("https://api.github.com/user").headers(Map.of("Authorization", "token " + accessToken))
                            .method(RequestMethod.GET)
                            .build().sendRequest());

                    String email = user.getString("email");
                    String username = user.getString("login");
                    int id = user.getInt("id");
                    String avatarUrl = user.getString("avatar_url");

                    var account = repository.findByEmail(email);

                    System.out.println(user.toString());
                    return ResponseEntity.ok(response.toString());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            }
        }
        return ResponseEntity.badRequest().build();
    }

    private void saveUserToken(AccountData account, String jwtToken) {
        var token = Token.builder()
                .userId(account.getId())
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();
        }
    }

    public void revokeAllUserTokens(AccountData account) {
        var validUserToken = tokenRepository.findAllValidTokensByUserId(account.getId());
        if (validUserToken.isEmpty())
            return;
        validUserToken.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserToken);
    }
}
