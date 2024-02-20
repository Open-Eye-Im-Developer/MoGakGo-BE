package io.oeid.mogakgo.core.properties.swagger.error;

public class SwaggerProjectJoinRequestErrorExamples {

    public static final String PROJECT_JOIN_REQUEST_ALREADY_EXIST = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E050201\",\"message\":\"이미 매칭 요청을 보낸 프로젝트에 매칭 요청을 보낼 수 없습니다.\"}";
    public static final String PROJECT_JOIN_REQUEST_INVALID_REGION = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E090102\",\"message\":\"프로젝트 매칭 서비스는 동네 인증되지 않은 구역에서 진행할 수 없습니다.\"}";
    public static final String ANOTHER_PROJECT_JOIN_REQUEST_ALREADY_EXIST = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E090103\",\"message\":\"프로젝트 매칭 요청은 한 번에 한 개만 보낼 수 있습니다.\"}";
    public static final String PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":403,\"code\":\"E090101\",\"message\":\"프로젝트 생성자는 해당 프로젝트에 매칭 요청을 보낼 수 없습니다.\"}";
    private SwaggerProjectJoinRequestErrorExamples() {
    }

}
