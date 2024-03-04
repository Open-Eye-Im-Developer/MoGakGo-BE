package io.oeid.mogakgo.common.swagger.template;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.oeid.mogakgo.core.properties.swagger.error.SwaggerProjectErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerReviewErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.review.presentation.dto.req.ReviewCreateApiReq;
import io.oeid.mogakgo.domain.review.presentation.dto.res.ReviewCreateApiRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Review", description = "리뷰 관련 API")
@SuppressWarnings("unused")
public interface ReviewSwagger {

    @Operation(summary = "리뷰 생성")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리뷰 생성 성공",
            content = @Content(schema = @Schema(implementation = ReviewCreateApiRes.class))),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ResponseEntity.class),
                examples = {
                    @ExampleObject(name = "E120101", value = SwaggerReviewErrorExamples.REVIEW_SENDER_OR_RECEIVER_NOT_FOUND),
                    @ExampleObject(name = "E120102", value = SwaggerReviewErrorExamples.REVIEW_USER_DUPLICATED),
                    @ExampleObject(name = "E120103", value = SwaggerReviewErrorExamples.REVIEW_PROJECT_NOT_NULL),
                    @ExampleObject(name = "E120104", value = SwaggerReviewErrorExamples.REVIEW_ALREADY_EXISTS),
                    @ExampleObject(name = "E120105", value = SwaggerReviewErrorExamples.REVIEW_USER_NOT_MATCH),
                    @ExampleObject(name = "E120106", value = SwaggerReviewErrorExamples.REVIEW_RATING_INVALID),
                })),
        @ApiResponse(responseCode = "404", description = "해당 데이터가 존재하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ResponseEntity.class),
                examples = {
                    @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND),
                    @ExampleObject(name = "E030301", value = SwaggerProjectErrorExamples.PROJECT_NOT_FOUND)
                }
            )),
    })
    ResponseEntity<ReviewCreateApiRes> createReviewApi(
        @Parameter(hidden = true) Long userId,
        ReviewCreateApiReq request);
}
