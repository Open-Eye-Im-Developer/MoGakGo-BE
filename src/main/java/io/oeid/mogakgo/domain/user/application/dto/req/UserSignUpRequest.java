package io.oeid.mogakgo.domain.user.application.dto.req;

import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignUpRequest {

    private Long userId;
    private String username;
    private List<WantedJob> wantedJobs;
}
