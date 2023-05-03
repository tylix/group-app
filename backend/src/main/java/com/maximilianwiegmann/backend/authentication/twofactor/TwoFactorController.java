package com.maximilianwiegmann.backend.authentication.twofactor;

import com.google.zxing.WriterException;
import com.maximilianwiegmann.backend.account.AccountData;
import com.maximilianwiegmann.backend.account.AccountService;
import com.maximilianwiegmann.backend.authentication.AuthenticationService;
import com.maximilianwiegmann.backend.security.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/twofactor", produces = "application/json")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class TwoFactorController {

    private final TwoFactorService twoFactorService;
    private final AuthenticationService authenticationService;
    private final AccountService accountService;

    private final List<JSONObject> generateRequests = new ArrayList<>();

    @PostMapping("/generate")
    public ResponseEntity<?> generate() throws IOException, WriterException {
        // TODO: Check for team to create qr code!

        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null) return ResponseEntity.status(403).build();
        if (accountData.getTfaSecret() != null) return ResponseEntity.status(403).build();

        String username = accountData.getUsername();
        String secret = twoFactorService.generateSecretKey();
        String company = "MORPHEUS";

        String barCode = twoFactorService.getGoogleAuthenticatorBarCode(secret, username, company);
        String qr = twoFactorService.createQRCode(barCode, 400, 400);

        JSONObject object = new JSONObject().put("secret", secret).put("qr", qr);
        object.put("username", username);
        generateRequests.removeIf(o -> o.has("username") && o.getString("username").equals(username));
        generateRequests.add(object);
        return ResponseEntity.ok(object.toString());
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody String json) {
        JSONObject body = new JSONObject(json);
        String code = body.getString("code");

        AccountData accountData = (AccountData) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (accountData == null) return ResponseEntity.status(403).build();
        if (accountData.getTfaSecret() != null) return ResponseEntity.status(403).build();
        JSONObject object = generateRequests.stream().filter(o -> o.has("username") && o.getString("username").equals(accountData.getUsername())).findFirst().orElse(null);
        if (object == null) return ResponseEntity.status(403).build();

        String secret = object.getString("secret");

        if (!code.equals(twoFactorService.getTOTPCode(secret)))
            return ResponseEntity.status(403).build();

        accountService.setTfaSecret(accountData, secret);
        authenticationService.revokeAllUserTokens(accountData);

        return ResponseEntity.ok().build();
    }

}
