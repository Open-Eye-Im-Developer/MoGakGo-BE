package io.oeid.mogakgo.domain.user.presentation.dto.res;

import io.oeid.mogakgo.domain.user.application.dto.res.UserUpdateRes;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "유저 정보 수정 응답")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserUpdateApiRes {

    @Schema(description = "유저 이름", example = "tidavid1")
    private String username;
    @Schema(description = "유저 소개", example = "안녕하세요")
    private String bio;
    @Schema(description = "유저 프로필 이미지 URL", example = "https://avatars.githubusercontent.com/u/1?v=4")
    private String avatarUrl;
    @Schema(description = "유저가 원하는 직군", example = "[\"BACKEND\", \"FRONTEND\"]")
    private List<String> wantedJobs;
    @Schema(description = "유저의 업적 ID", example = "1")
    private Long achievementId;

    public static UserUpdateApiRes from(UserUpdateRes userUpdateRes) {
        return new UserUpdateApiRes(
            userUpdateRes.getUsername(),
            userUpdateRes.getBio(),
            userUpdateRes.getAvatarUrl(),
            userUpdateRes.getWantedJobs().stream().map(WantedJob::getJobName).toList(),
            userUpdateRes.getAchievementId()
        );
    }
}
