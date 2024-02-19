package io.oeid.mogakgo.domain.user.presentation.dto.req;

import io.oeid.mogakgo.domain.user.application.dto.req.UserSignUpRequest;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원 가입 요청")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpApiRequest {

    @Schema(description = "회원명", example = "거루", implementation = String.class)
    @NotBlank(message = "username은 필수입니다.")
    private String username;

    @Schema(description = "원하는 직군", example = "[\"BACKEND\", \"FRONTEND\"]", implementation = List.class, minLength = 1, maxLength = 3)
    @Size(min = 1, max = 3, message = "wantedJobs는 1개 이상 3개 이하로 선택해야 합니다.")
    private List<String> wantedJobs;

    public UserSignUpRequest toRequest(Long userId) {
        return new UserSignUpRequest(userId, username,
            wantedJobs.stream().map(WantedJob::valueOf).toList());
    }

}
