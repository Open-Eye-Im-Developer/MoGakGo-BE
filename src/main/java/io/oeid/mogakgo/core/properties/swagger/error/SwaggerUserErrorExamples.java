package io.oeid.mogakgo.core.properties.swagger.error;

public class SwaggerUserErrorExamples {

    public static final String USER_NOT_FOUND = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":404,\"code\":\"E020301\",\"message\":\"해당 유저가 존재하지 않습니다.\"}";
    public static final String USER_WANTED_JOB_DUPLICATE = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E020105\",\"message\":\"중복된 희망 직무가 있습니다.\"}";
    public static final String INVALID_USER_NAME = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E020103\",\"message\":\"유저 이름은 비어있을 수 없습니다.\"}";
    public static final String USER_AVAILABLE_LIKE_COUNT_IS_ZERO = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E020107\",\"message\":\"취소할 수 있는 찔러보기 요청이 존재하지 않습니다.\"}";
    public static final String USER_AVATAR_URL_NOT_NULL = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E020109\",\"message\":\"유저 프로필 이미지는 비어있을 수 없습니다.\"}";
    public static final String USER_FORBIDDEN_OPERATION = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":403,\"code\":\"E020201\",\"message\":\"사용자의 정보를 변경할 권한이 없습니다.\"}";
    private SwaggerUserErrorExamples() {
    }

}
