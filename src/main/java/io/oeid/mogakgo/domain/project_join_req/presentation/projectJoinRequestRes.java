package io.oeid.mogakgo.domain.project_join_req.presentation;

import io.oeid.mogakgo.domain.project_join_req.domain.entity.enums.RequestStatus;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "프로젝트 요청 응답 DTO")
@Getter
public class projectJoinRequestRes {

    @Schema(description = "프로젝트 요청 ID")
    private Long id;
    @Schema(description = "요청자 정보 미리보기")
    private UserPublicApiResponse senderPreview;
    @Schema(description = "요청 상태")
    private RequestStatus requestStatus;

    public projectJoinRequestRes(
        Long id, User user, RequestStatus requestStatus
    ) {
        this.id = id;
        this.senderPreview = UserPublicApiResponse.from(user);
        this.requestStatus = requestStatus;
    }
}
