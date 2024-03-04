package io.oeid.mogakgo.core.properties.swagger.error;

public class SwaggerReviewErrorExamples {

    public static final String REVIEW_SENDER_OR_RECEIVER_NOT_FOUND = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E120101\",\"message\":\"리뷰를 작성하기 위한 유저 정보가 존재하지 않습니다.\"}";
    public static final String REVIEW_USER_DUPLICATED = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E120102\",\"message\":\"자신에 대한 리뷰는 작성할 수 없습니다.\"}";
    public static final String REVIEW_PROJECT_NOT_NULL = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E120103\",\"message\":\"리뷰를 작성하기 위한 프로젝트 정보가 존재하지 않습니다.\"}";
    public static final String REVIEW_ALREADY_EXISTS = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E120104\",\"message\":\"이미 작성된 리뷰가 존재합니다.\"}";
    public static final String REVIEW_USER_NOT_MATCH = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E120105\",\"message\":\"리뷰 작성자와 리뷰 대상자가 일치하지 않습니다.\"}";
    public static final String REVIEW_RATING_INVALID = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E120106\",\"message\":\"유효하지 않은 리뷰 평점입니다.\"}";

    private SwaggerReviewErrorExamples() {
    }
}
