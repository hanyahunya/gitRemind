package com.hanyahunya.gitRemind.member.service;

public interface PwEncodeService {
    String encode(String pw);
    boolean matches(String pw, String encodedPw);
}
