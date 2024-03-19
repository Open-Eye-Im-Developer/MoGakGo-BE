package io.oeid.mogakgo.core.properties.swagger.error;

public class SwaggerChatErrorExamples {

    public static final String CHAT_ROOM_NOT_FOUND = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":404,\"code\":\"E110301\",\"message\":\"해당 채팅방이 존재하지 않습니다.\"}";
    public static final String CHAT_ROOM_USER_CANNOT_DUPLICATE = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E110102\",\"message\":\"채팅방에 같은 유저가 존재할 수 없습니다.\"}";
    public static final String CHAT_USER_NOT_FOUND = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":404,\"code\":\"E110302\",\"message\":\"해당 유저가 존재하지 않습니다.\"}";

    private SwaggerChatErrorExamples() {
    }
}
