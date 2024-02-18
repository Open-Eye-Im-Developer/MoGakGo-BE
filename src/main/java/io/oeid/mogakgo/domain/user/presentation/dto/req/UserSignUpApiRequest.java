package io.oeid.mogakgo.domain.user.presentation.dto.req;

import io.oeid.mogakgo.domain.user.application.dto.req.UserSignUpRequest;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpApiRequest {

    @NotBlank(message = "username은 필수입니다.")
    private String username;

    @Size(min = 1, max = 3, message = "wantedJobs는 1개 이상 3개 이하로 선택해야 합니다.")
    private List<String> wantedJobs;

    public UserSignUpRequest toRequest(Long userId) {
        return new UserSignUpRequest(userId, username,
            wantedJobs.stream().map(WantedJob::valueOf).toList());
    }

}
