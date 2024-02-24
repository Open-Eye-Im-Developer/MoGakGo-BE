package io.oeid.mogakgo.core.properties.swagger.error;

public class SwaggerProfileCardLikeErrorExamples {

    public static final String PROFILE_CARD_LIKE_ALREADY_EXIST = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E040102\",\"message\":\"이미 찔러보기 요청을 전송한 프로필 카드에 찔러보기 요청을 전송할 수 없습니다.\"}";
    public static final String PROFILE_CARD_LIKE_NOT_EXIST = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E040105\",\"message\":\"해당 프로필 카드에 찔러보기 요청이 존재하지 않아 요청을 취소할 수 없습니다.\"}";
    public static final String INVALID_PROFILE_CARD_LIKE_RECEIVER_INFO = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E040103\",\"message\":\"찔러보기 요청의 사용자가 존재하지 않습니다.\"}";
    public static final String PROFILE_CARD_LIKE_FORBIDDEN_OPERATION = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":403,\"code\":\"E040103\",\"message\":\"본인의 프로필 카드 외에 대해 찔러보기 요청 권한이 없습니다.\"}";
    public static final String PROFILE_CARD_NOT_FOUND = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":404,\"code\":\"E040301\",\"message\":\"해당 프로필 카드가 존재하지 않습니다.\"}";

    private SwaggerProfileCardLikeErrorExamples() {
    }

}
