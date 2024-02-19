package io.oeid.mogakgo.domain.user.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 정보 미리보기 응답 DTO")
public class UserPreviewRes {

    private Long id;
    private String username;
    private String avatarUrl;

    public UserPreviewRes(Long id, String username, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public static UserPreviewRes of(Long id, String username, String avatarUrl) {
        return new UserPreviewRes(id, username, avatarUrl);
    }
}
