package com.auth_example.auth_service.jwt;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final KeyPair keyPair;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> exposeJwks() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey jwk = new RSAKey.Builder(publicKey)
                .keyID("auth-key")
                .build();

        return new JWKSet(jwk).toJSONObject();
    }
}
