package io.oeid.mogakgo.core.properties.swagger.error;

public class SwaggerProjectJoinReqErrorExamples {

    public static final String INVALID_PROJECT_STATUS_TO_ACCEPT = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E050101\",\"message\":\"프로젝트가 대기중이 아니여서 참여 요청을 수락할 수 없습니다.\"}";
    public static final String INVALID_PROJECT_REQ_STATUS_TO_CANCEL = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E050105\",\"message\":\"\"프로젝트 참여 요청이 대기중이 아니여서 참여 요청을 취소할 수 없습니다.\"}";
    public static final String INVALID_PROJECT_REQ_STATUS_TO_ACCEPT = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E050102\",\"message\":\"프로젝트 참가 요청이 대기중이 아니여서 수락할 수 없습니다.\"}";
    public static final String INVALID_MATCHING_USER_TO_ACCEPT = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E050103\",\"message\":\"매칭이 진행 중인 유저는 프로젝트 참여 요청을 수락할 수 없습니다.\"}";
    public static final String INVALID_SENDER_TO_ACCEPT = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E050104\",\"message\":\"요청 보낸 상대가 이미 매칭중이기 때문에 프로젝트 참여 요청을 수락할 수 없습니다.\"}";
    public static final String PROJECT_JOIN_REQUEST_NOT_FOUND = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":404,\"code\":\"E050301\",\"message\":\"프로젝트 참가 요청이 존재하지 않습니다.\"}";


}
