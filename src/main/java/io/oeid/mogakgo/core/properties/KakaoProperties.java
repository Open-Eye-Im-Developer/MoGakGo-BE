package io.oeid.mogakgo.core.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "kakao")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoProperties {

    private String prefix;
    private String restApiKey;
}
