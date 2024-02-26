package io.oeid.mogakgo.domain.user.presentation.dto.res;

import io.oeid.mogakgo.domain.user.application.dto.res.UserSignUpResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "회원 가입 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpApiResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long id;
    @Schema(description = "회원 이름", example = "거루")
    private String username;
    @Schema(description = "깃허브 ID", example = "tidavid1")
    private String githubId;
    @Schema(description = "회원 프로필 이미지 URL", example = "https://avatars.githubusercontent.com/u/85854384?v=4")
    private String avatarUrl;
    @Schema(description = "깃허브 URL", example = "https://github.com/tidavid1")
    private String githubUrl;
    @Schema(description = "희망 직무", example = "[\"BACKEND\", \"FRONTEND\"]")
    private List<String> wantedJobs;

    public static UserSignUpApiResponse from(UserSignUpResponse response) {
        return new UserSignUpApiResponse(
            response.getUserId(),
            response.getUsername(),
            response.getGithubId(),
            response.getAvatarUrl(),
            response.getGithubUrl(),
            response.getWantedJobs().stream().map(Enum::name).toList()
        );
    }
}
