package io.oeid.mogakgo.domain.review.presentation.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Schema(description = "리뷰 생성 요청")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateApiReq {

    @NotNull(message = "리뷰 생성자 ID는 필수입니다.")
    @Schema(description = "리뷰 생성자 ID", example = "1")
    private Long senderId;

    @NotNull(message = "리뷰 대상자 ID는 필수입니다.")
    @Schema(description = "리뷰 대상자 ID", example = "2")
    private Long receiverId;

    @NotNull(message = "프로젝트 ID는 필수입니다.")
    @Schema(description = "프로젝트 ID", example = "3")
    private Long projectId;

    @Range(min = 1, max = 5, message = "평점은 1~5 사이의 값이어야 합니다.")
    @Schema(description = "평점", example = "5", minimum = "1", maximum = "5")
    private Integer rating;
}
