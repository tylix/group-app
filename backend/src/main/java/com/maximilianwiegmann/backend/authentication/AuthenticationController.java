package com.maximilianwiegmann.backend.authentication;

import com.maximilianwiegmann.backend.payload.request.AuthenticationRequest;
import com.maximilianwiegmann.backend.payload.request.RegisterRequest;
import com.maximilianwiegmann.backend.payload.request.TwoFARequest;
import com.maximilianwiegmann.backend.payload.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = service.register(request);
        if (response == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/otp")
    public ResponseEntity<?> otp(@RequestBody TwoFARequest request) {
        return service.otp(request);
    }

    @PostMapping("/status")
    public ResponseEntity<?> status(@RequestBody String bodyString) {
        JSONObject body = new JSONObject(bodyString);
        return service.status(body.getString("token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        service.logout(request);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/signinwith")
    public ResponseEntity<?> signInWith(@RequestParam String provider, @RequestParam String code) {
        return service.signInWith(provider, code);
    }
}
