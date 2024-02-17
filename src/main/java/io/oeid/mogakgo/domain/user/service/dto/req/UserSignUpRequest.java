package io.oeid.mogakgo.domain.user.service.dto.req;

import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import java.util.List;
import lombok.Getter;

@Getter
public class UserSignUpRequest {

    private Long userId;
    private String username;
    private List<WantedJob> wantedJobs;
}
