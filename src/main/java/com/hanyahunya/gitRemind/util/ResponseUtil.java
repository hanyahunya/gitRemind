package com.hanyahunya.gitRemind.util;

import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    private ResponseUtil() {
        throw new UnsupportedOperationException("このクラスはインスタンス化できません");
    }

    public static <T> ResponseEntity<ResponseDto<T>> toResponse(ResponseDto<T> responseDto) {
        if (responseDto.isSuccess()) {
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.badRequest().body(responseDto);
        }
    }
}
