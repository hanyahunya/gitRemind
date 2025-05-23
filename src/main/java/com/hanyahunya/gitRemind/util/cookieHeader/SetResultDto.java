package com.hanyahunya.gitRemind.util.cookieHeader;

import com.hanyahunya.gitRemind.token.service.TokenPurpose;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class SetResultDto {
    @Builder.Default
    private boolean success = false;
    @Builder.Default
    private String accessToken = null;
    @Builder.Default
    private String refreshToken  = null;
    @Builder.Default
    private String validateToken = null;
    @Builder.Default
    private TokenPurpose purpose = null;
    @Builder.Default
    private boolean deleteAccessToken = false;
    @Builder.Default
    private boolean deleteRefreshToken = false;
    @Builder.Default
    private boolean deleteValidateToken = false;
}
