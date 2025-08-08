package com.auth_example.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyConfig {

//    @Bean
//    public KeyPair rsaKeyPair() {
//        try {
//            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
//            gen.initialize(2048);
//            return gen.generateKeyPair();
//        } catch (Exception e) {
//            throw new IllegalStateException("unable to generate rsa key pair");
//        }
//    }

    @Bean
    public KeyPair rsaKeyPair() {
        try {
            String privateKeyPem = readKeyFromResource("keys/private_key.pem");
            String publicKeyPem = readKeyFromResource("keys/public_key.pem");

            PrivateKey privateKey = getPrivateKeyFromPem(privateKeyPem);
            PublicKey publicKey = getPublicKeyFromPem(publicKeyPem);

            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalStateException("failed to load rsa key pair", e);
        }
    }

    private String readKeyFromResource(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private PrivateKey getPrivateKeyFromPem(String pem) throws Exception {
        pem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey getPublicKeyFromPem(String pem) throws Exception {
        pem = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
