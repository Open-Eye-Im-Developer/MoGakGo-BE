package io.oeid.mogakgo.core.properties.swagger.error;

public class SwaggerMatchingErrorExamples {

    public static final String MATCHING_CANCEL_NOT_ALLOWED = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E090101\",\"message\":\"이미 취소 되었거나 종료된 매칭은 취소할 수 없습니다.\"}";
    public static final String MATCHING_NOT_FOUND = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":404,\"code\":\"E090301\",\"message\":\"해당 매칭이 존재하지 않습니다.\"}";
    public static final String MATCHING_FORBIDDEN_OPERATION = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":403,\"code\":\"E090201\",\"message\":\"해당 매칭에 대한 권한이 없습니다.\"}";

}
