package com.hanyahunya.gitRemind;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "set")
public class ResponseDto <D> {
    private boolean success;
    private String message;
    private D data;

    public static <D> ResponseDto<D> success(String message, D data) {
        return ResponseDto.set(true, message, data);
    }
    public static ResponseDto<Void> success(String message) {
        return ResponseDto.set(true, message, null);
    }

    public static <D> ResponseDto<D> fail(String message, D data) {
        return ResponseDto.set(false, message, data);
    }
    public static ResponseDto<Void> fail(String message) {
        return ResponseDto.set(false, message, null);
    }
}
