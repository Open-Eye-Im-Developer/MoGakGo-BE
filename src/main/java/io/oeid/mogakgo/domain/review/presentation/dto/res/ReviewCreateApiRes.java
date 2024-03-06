package io.oeid.mogakgo.domain.review.presentation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "리뷰 생성 API 응답")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateApiRes {

    @Schema(description = "리뷰 ID", example = "1")
    private Long id;

    @Schema(description = "리뷰 작성자 ID", example = "1")
    private Long senderId;

    @Schema(description = "리뷰 대상자 ID", example = "2")
    private Long receiverId;

    @Schema(description = "프로젝트 ID", example = "1")
    private Long projectId;

    @Schema(description = "프로젝트 ID", example = "1")
    private Integer rating;

    @Schema(description = "리뷰 생성일시", example = "2021-08-01T00:00:00")
    private String createdAt;
}
