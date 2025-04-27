package com.hanyahunya.gitRemind.util.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public class PBKDF2EncodeService implements EncodeService {
    private Pbkdf2PasswordEncoder passwordEncoder;
    @Value("${password.encoder.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        passwordEncoder = new Pbkdf2PasswordEncoder(secret, 16, 10000, Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
    }

    @Override
    public String encode(String data) {
        return passwordEncoder.encode(data);
    }

    @Override
    public boolean matches(String data, String hashedData) {
        return passwordEncoder.matches(data, hashedData);
    }
}
