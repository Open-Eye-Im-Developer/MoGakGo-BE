package io.oeid.mogakgo.core.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "rabbit")
@NoArgsConstructor
public class RabbitMQProperties {

    private String url;
    private Integer port;
    private String clientLogin;
    private String clientPassword;
}
