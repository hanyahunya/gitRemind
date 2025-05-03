package com.hanyahunya.gitRemind.token;

import com.hanyahunya.gitRemind.token.service.TokenService;
import com.hanyahunya.gitRemind.util.ResponseDto;
import com.hanyahunya.gitRemind.util.cookieHeader.SetResultDto;
import com.hanyahunya.gitRemind.util.cookieHeader.TokenCookieHeaderGenerator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hanyahunya.gitRemind.util.ResponseUtil.toResponse;
import static com.hanyahunya.gitRemind.util.ResponseUtil.toResponseWithHeader;

@RestController
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;
    private final TokenCookieHeaderGenerator tokenCookieHeaderGenerator;

    @PostMapping("/refreshAccessToken")
    public ResponseEntity<ResponseDto<Void>> refreshAccessToken(HttpServletRequest request) {
        String accessToken = null;
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    accessToken = cookie.getValue();
                } else if (cookie.getName().equals("refresh_token")) {
                    refreshToken = cookie.getValue();
                }
            }
        }
        if (accessToken != null && refreshToken != null) {
            SetResultDto setResultDto = tokenService.refreshAccessToken(accessToken, refreshToken);
            if (setResultDto != null) {
                HttpHeaders headers = tokenCookieHeaderGenerator.handleTokenHeader(setResultDto);
                if (setResultDto.isSuccess()) {
                    return toResponseWithHeader(ResponseDto.success("Access token 更新成功"), headers);
                } else {
                    return toResponseWithHeader(ResponseDto.fail("Access token 更新失敗"), headers);
                }
            }
        }
        return toResponse(ResponseDto.fail("Access token 更新失敗"));
    }
}
