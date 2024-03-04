package io.oeid.mogakgo.domain.chat.interceptor;

import io.oeid.mogakgo.domain.auth.jwt.JwtHelper;
import io.oeid.mogakgo.domain.chat.exception.ChatException;
import io.oeid.mogakgo.exception.code.ErrorCode401;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatInterceptor implements ChannelInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtHelper jwtHelper;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (Objects.requireNonNull(accessor.getCommand()).equals(StompCommand.CONNECT)) {
            log.info("CONNECT REQUEST: {}", accessor.getSessionId());
            verifyAccessToken(accessor);
        }
        return message;
    }

    private void verifyAccessToken(StompHeaderAccessor stompHeaderAccessor) {
        var values = stompHeaderAccessor.getNativeHeader(AUTHORIZATION_HEADER);
        if (values == null || values.isEmpty()) {
            throw new ChatException(ErrorCode401.AUTH_MISSING_CREDENTIALS);
        }
        String accessToken = values.get(0);
        if (accessToken == null || accessToken.isBlank()) {
            throw new ChatException(ErrorCode401.AUTH_MISSING_CREDENTIALS);
        }
        accessToken = accessToken.substring(7);
        jwtHelper.verify(accessToken);
    }
}
