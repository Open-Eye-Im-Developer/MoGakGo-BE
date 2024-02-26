package io.oeid.mogakgo.domain.user.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Schema(description = "유저 정보 수정 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateApiReq {

    @Schema(description = "유저 이름", example = "tidavid1")
    @NotBlank
    private String username;
    @Schema(description = "유저 소개", example = "안녕하세요", nullable = true)
    private String bio;
    @Schema(description = "유저 프로필 이미지 URL", example = "https://avatars.githubusercontent.com/u/12345678?v=4")
    @URL
    private String avatarUrl;
    @Schema(description = "선호하는 직군", example = "[\"BACKEND\", \"FRONTEND\"]")
    @Size(min = 1, max = 3)
    private List<String> wantedJobs;
    @Schema(description = "업적 ID", example = "1")
    @NotNull
    private Long achievementId;
}
