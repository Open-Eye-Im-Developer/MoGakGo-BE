package io.oeid.mogakgo.domain.review.application;

import io.oeid.mogakgo.domain.project.infrastructure.ProjectJpaRepository;
import io.oeid.mogakgo.domain.review.application.dto.req.ReviewCreateReq;
import io.oeid.mogakgo.domain.review.application.dto.res.ReviewCreateRes;
import io.oeid.mogakgo.domain.review.domain.Review;
import io.oeid.mogakgo.domain.review.exception.ReviewException;
import io.oeid.mogakgo.domain.review.infrastructure.ReviewJpaRepository;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewJpaRepository reviewRepository;
    private final ProjectJpaRepository projectRepository;
    private final UserCommonService userCommonService;

    @Transactional
    public ReviewCreateRes createNewReview(ReviewCreateReq request) {
        reviewRepository.findReviewByProjectData(request.getSenderId(), request.getReceiverId(),
            request.getProjectId()).ifPresent(review -> {
            throw new ReviewException(ErrorCode400.REVIEW_ALREADY_EXISTS);
        });
        var sender = userCommonService.getUserById(request.getSenderId());
        var receiver = userCommonService.getUserById(request.getReceiverId());
        var project = projectRepository.findById(request.getProjectId())
            .orElseThrow(() -> new ReviewException(ErrorCode404.PROJECT_NOT_FOUND));
        var review = reviewRepository.save(Review.builder()
            .sender(sender)
            .receiver(receiver)
            .project(project)
            .rating(request.getRating())
            .build()
        );
        receiver.updateJandiRateByReview(review.getRating(),
            calculateProjectTime(project.getMeetingInfo().getMeetStartTime(),
                project.getMeetingInfo().getMeetEndTime()));
        return ReviewCreateRes.from(review);
    }

    private double calculateProjectTime(LocalDateTime meetStartTime, LocalDateTime meetEndTime) {
        Duration duration = Duration.between(meetStartTime, meetEndTime);
        double hours = duration.toHours();
        double minutes = duration.toMinutes() % 60;
        return hours + minutes / 60;
    }

}
