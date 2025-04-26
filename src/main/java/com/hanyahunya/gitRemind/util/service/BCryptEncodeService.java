package com.hanyahunya.gitRemind.util.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptEncodeService implements EncodeService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String data) {
        return passwordEncoder.encode(data);
    }
    @Override
    public boolean matches(String data, String hashedData) {
        return passwordEncoder.matches(data, hashedData);
    }
}
