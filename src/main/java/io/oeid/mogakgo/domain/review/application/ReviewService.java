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

    private static final double JANDI_WEIGHT = 2.5;

    private final ReviewJpaRepository reviewRepository;
    private final ProjectJpaRepository projectRepository;
    private final UserCommonService userCommonService;
    private final ReviewEventHelper eventHelper;

    @Transactional
    public ReviewCreateRes createNewReview(Long userId, ReviewCreateReq request) {
        validateUser(userId, request.getSenderId());
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

        double time = calculateProjectTime(
            project.getMeetingInfo().getMeetStartTime(),
            project.getMeetingInfo().getMeetEndTime()
        );
        receiver.updateJandiRateByReview(review.getRating(), time);

        // -- '잔디력 업데이트' 로직이 커밋되기 전, 새로운 트랜잭션으로 인해 변경 전의 데이터에 대해 조회됨
        eventHelper.publishEvent(receiver.getId(), calculateUpdatedJandiRate(review, time));

        return ReviewCreateRes.from(review);
    }

    private double calculateProjectTime(LocalDateTime meetStartTime, LocalDateTime meetEndTime) {
        Duration duration = Duration.between(meetStartTime, meetEndTime);
        double hours = duration.toHours();
        double minutes = duration.toMinutes() % 60;
        return hours + minutes / 60;
    }

    private double calculateUpdatedJandiRate(Review review, double time) {
        return review.getRating().getJandiValue() * time * JANDI_WEIGHT;
    }

    private void validateUser(Long userId, Long senderId) {
        if (!userId.equals(senderId)) {
            throw new ReviewException(ErrorCode400.REVIEW_USER_NOT_MATCH);
        }
    }
}
