package io.oeid.mogakgo.core.properties.swagger.error;

public class SwaggerUserAchievementErrorExamples {

    public static final String ACHIEVEMENT_FORBIDDEN_OPERATION = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":403,\"code\":\"E140201\",\"message\":\"자신의 업적에 대해서만 조회할 수 있습니다.\"}";
    public static final String NON_ACHIEVED_USER_ACHIEVEMENT = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E140101\",\"message\":\"대표 업적으로 미달성 업적을 사용할 수 없습니다.\"}";
    public static final String ACHIEVEMENT_SHOULD_BE_DIFFERENT = "{\"timestamp\":\"2024-02-17T10:07:31.404Z\",\"statusCode\":400,\"code\":\"E140102\",\"message\":\"해당 업적을 이미 대표 업적으로 사용하고 있습니다.\"}";

    private SwaggerUserAchievementErrorExamples() {

    }

}
