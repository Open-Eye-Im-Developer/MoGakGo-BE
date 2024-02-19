package io.oeid.mogakgo.domain.user.presentation.dto.res;

import io.oeid.mogakgo.domain.user.application.dto.res.UserProfileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "회원 프로필 조회 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserProfileCardApiResponse {
    @Schema(description = "회원 식별자", example = "1")
    private final long id;
    @Schema(description = "회원명", example = "거루")
    private final String username;
    @Schema(description = "깃허브 ID", example = "tidavid1")
    private final String githubId;
    @Schema(description = "아바타 URL", example = "https://avatars.githubusercontent.com/u/85854384?v=4")
    private final String avatarUrl;
    @Schema(description = "깃허브 URL", example = "https://github.com/tidavid1")
    private final String githubUrl;
    @Schema(description = "소개", example = "안녕하세요", nullable = true)
    private final String bio;
    @Schema(description = "잔디 비율", example = "0.5")
    private final double jandiRate;
    @Schema(description = "업적 제목", example = "이세계 개발자")
    private final String achievementTitle;
    @Schema(description = "개발 언어", example = "[\"JAVA\", \"KOTLIN\"]")
    private final List<String> developLanguages;
    @Schema(description = "원하는 직군", example = "[\"BACKEND\", \"FRONTEND\"]")
    private final List<String> wantedJobs;

    public static UserProfileCardApiResponse from(UserProfileResponse response) {
        return new UserProfileCardApiResponse(
            response.getId(),
            response.getUsername(),
            response.getGithubId(),
            response.getAvatarUrl(),
            response.getGithubUrl(),
            response.getBio(),
            response.getJandiRate(),
            response.getAchievementTitle(),
            response.getDevelopLanguages().stream().map(Enum::name).toList(),
            response.getWantedJobs().stream().map(Enum::name).toList()
        );
    }
}
