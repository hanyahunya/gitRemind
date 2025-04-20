package com.hanyahunya.gitRemind.member.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptPwEncodeService implements PwEncodeService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(String pw) {
        return passwordEncoder.encode(pw);
    }
    @Override
    public boolean matches(String pw, String encodedPw) {
        return passwordEncoder.matches(pw, encodedPw);
    }
}
