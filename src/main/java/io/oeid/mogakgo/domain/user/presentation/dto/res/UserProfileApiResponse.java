package io.oeid.mogakgo.domain.user.presentation.dto.res;

import io.oeid.mogakgo.domain.user.application.dto.res.UserProfileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "유저 프로필 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProfileApiResponse {

    @Schema(description = "유저 식별자", example = "2")
    private final long id;
    @Schema(description = "유저 이름", example = "거루")
    private final String username;
    @Schema(description = "깃허브 ID", example = "tidavid1")
    private final String githubId;
    @Schema(description = "아바타 URL", example = "https://avatars.githubusercontent.com/u/85854384?v=4")
    private final String avatarUrl;
    @Schema(description = "깃허브 URL", example = "https://github.com/tidavid1")
    private final String githubUrl;
    @Schema(description = "소개", example = "안녕하세요", nullable = true)
    private final String bio;
    @Schema(description = "잔디력", example = "0.5")
    private final double jandiRate;
    @Schema(description = "개발 언어 태그", example = "[\"JAVA\", \"JAVASCRIPT\"]")
    private final List<String> developLanguages;
    @Schema(description = "원하는 직군 태그", example = "[\"BACKEND\", \"FRONTEND\"]")
    private final List<String> wantedJobs;

    public static UserProfileApiResponse from(UserProfileResponse response) {
        return new UserProfileApiResponse(
            response.getId(),
            response.getUsername(),
            response.getGithubId(),
            response.getAvatarUrl(),
            response.getGithubUrl(),
            response.getBio(),
            response.getJandiRate(),
            response.getDevelopLanguages().stream().map(Enum::name).toList(),
            response.getWantedJobs().stream().map(Enum::name).toList()
        );
    }
}
