package io.oeid.mogakgo.domain.profile.presentation.dto.res;

import io.oeid.mogakgo.domain.user.presentation.dto.res.UserPublicApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "선택한 구역에 대한 프로필 카드 리스트 조회 응답 DTO")
@Getter
@AllArgsConstructor
public class UserProfileInfoAPIRes {

    @Schema(description = "조회하는 프로필 카드의 상세 정보")
    private final UserPublicApiResponse response;

    @Schema(description = "해당 프로필 카드에 대해 사용자의 '찔러보기' 요청 여부")
    private final Boolean requestYn;

}
