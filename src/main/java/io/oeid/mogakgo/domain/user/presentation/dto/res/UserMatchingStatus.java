package io.oeid.mogakgo.domain.user.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "매칭 상태를 나타내는 응답")
@Getter
public class UserMatchingStatus {

    @Schema(description = "매칭 상태")
    private boolean matchingStatus;

    public UserMatchingStatus(boolean matchingStatus) {
        this.matchingStatus = matchingStatus;
    }
}
