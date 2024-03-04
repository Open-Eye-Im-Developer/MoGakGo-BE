package io.oeid.mogakgo.domain.review.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.swagger.template.ReviewSwagger;
import io.oeid.mogakgo.domain.review.application.ReviewService;
import io.oeid.mogakgo.domain.review.application.dto.req.ReviewCreateReq;
import io.oeid.mogakgo.domain.review.presentation.dto.req.ReviewCreateApiReq;
import io.oeid.mogakgo.domain.review.presentation.dto.res.ReviewCreateApiRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class ReviewController implements ReviewSwagger {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewCreateApiRes> createReviewApi(@UserId Long userId,
        @Valid @RequestBody ReviewCreateApiReq request) {
        var result = reviewService.createNewReview(userId, ReviewCreateReq.from(request));
        return ResponseEntity.ok(result.toApiResponse());
    }

}
