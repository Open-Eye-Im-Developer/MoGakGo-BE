package io.oeid.mogakgo.core.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
@NoArgsConstructor
public class JwtProperties {

    private String header;
    private String issuer;
    private String clientSecret;
    private int accessTokenExpiryHour;
    private int refreshTokenExpiryHour;

}