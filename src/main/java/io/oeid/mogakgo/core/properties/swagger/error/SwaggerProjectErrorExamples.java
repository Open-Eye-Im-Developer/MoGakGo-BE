package io.oeid.mogakgo.core.properties.swagger.error;

public class SwaggerProjectErrorExamples {

    public static final String INVALID_PROJECT_MEETING_TIME = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E030101\",\"message\":\"프로젝트 만남 시간이 유효하지 않습니다.\"}";
    public static final String INVALID_PROJECT_TAG_COUNT = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E030102\",\"message\":\"프로젝트 태그 갯수가 유효하지 않습니다. 1개 이상 3개 이하로 입력해야 합니다.\"}";
    public static final String INVALID_PROJECT_TAG_CONTENT_LENGTH = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E030103\",\"message\":\"프로젝트 태그 내용 길이가 유효하지 않습니다. 1자 이상 7자 이하로 입력해야 합니다.\"}";
    public static final String INVALID_PROJECT_NULL_DATA = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E030104\",\"message\":\"프로젝트를 생성하기 위해 null 이여서는 안되는 데이터가 null 입니다.\"}";
    public static final String INVALID_PROJECT_MEET_LOCATION = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E030105\",\"message\":\"프로젝트 만남 장소가 유저가 동네인증 한 구역이 아닙니다.\"}";
    public static final String PROJECT_NOT_FOUND = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":404,\"code\":\"E030301\",\"message\":\"해당 프로젝트가 존재하지 않습니다.\"}";
    public static final String PROJECT_FORBIDDEN_OPERATION = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":403,\"code\":\"E030201\",\"message\":\"해당 프로젝트에 대한 권한이 없습니다.\"}";
    public static final String PROJECT_DELETION_NOT_ALLOWED = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E030106\",\"message\":\"매칭 중이거나 대기중인 프로젝트는 삭제할 수 없습니다.\"}";
    public static final String PROJECT_CANCEL_NOT_ALLOWED = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E030107\",\"message\":\"진행중인 프로젝트만 취소 할 수 있습니다.\"}";
    public static final String PROJECT_JOIN_REQUEST_FORBIDDEN_OPERATION = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":403,\"code\":\"E050201\",\"message\":\"해당 프로젝트 요청에 대한 권한이 없습니다.\"}";
    public static final String INVALID_MATCHING_USER_TO_CREATE_PROJECT = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E030108\",\"message\":\"매칭이 진행 중인 유저는 프로젝트 생성을 할 수 없습니다.\"}";
    public static final String ALREADY_EXIST_PROGRESS_PROJECT = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E030109\",\"message\":\"종료되지 않은 프로젝트 카드가 있으면 프로젝트 생성을 할 수 없습니다.\"}";
    public static final String INVALID_PROJECT_STATUS_TO_FINISH = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E030110\",\"message\":\"매칭중인 프로젝트가 아니여서 프로젝트를 종료할 수 없습니다.\"}";

    private SwaggerProjectErrorExamples() {
    }

}
