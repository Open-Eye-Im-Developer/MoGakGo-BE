package io.oeid.core_security.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude
public class JwtToken {

    private Long userId;
    private String accessToken;
    private String refreshToken;
    private Date refreshTokenExpiryDate;

    public static JwtToken of(Long userId, String accessToken, String refreshToken,
        Date refreshTokenExpiryDate) {
        return new JwtToken(userId, accessToken, refreshToken, refreshTokenExpiryDate);
    }
}
