package io.oeid.mogakgo.domain.matching.application;

import static io.oeid.mogakgo.exception.code.ErrorCode404.MATCHING_NOT_FOUND;

import io.oeid.mogakgo.domain.matching.domain.entity.Matching;
import io.oeid.mogakgo.domain.matching.exception.MatchingException;
import io.oeid.mogakgo.domain.matching.infrastructure.MatchingJpaRepository;
import io.oeid.mogakgo.domain.project_join_req.domain.entity.ProjectJoinRequest;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MatchingService {

    private final MatchingJpaRepository matchingJpaRepository;

    private final UserCommonService userCommonService;

    @Transactional
    public Long create(ProjectJoinRequest projectJoinRequest) {
        Matching matching = Matching.builder()
            .project(projectJoinRequest.getProject())
            .sender(projectJoinRequest.getSender())
            .build();

        matchingJpaRepository.save(matching);

        return matching.getId();
    }

    @Transactional
    public Long cancel(Long tokenUserId, Long matchingId) {
        User tokenUser = userCommonService.getUserById(tokenUserId);

        Matching matching = getMatching(matchingId);

        // 매칭 취소
        // 프로젝트 종료 상태 변경
        matching.cancel(tokenUser);

        return matching.getId();
    }

    private Matching getMatching(Long matchingId) {
        return matchingJpaRepository.findById(matchingId)
            .orElseThrow(() -> new MatchingException(MATCHING_NOT_FOUND));
    }
}
