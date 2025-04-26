package com.hanyahunya.gitRemind.util.service;

public interface EncodeService {
    String encode(String data);
    boolean matches(String data, String hashedData);
}
