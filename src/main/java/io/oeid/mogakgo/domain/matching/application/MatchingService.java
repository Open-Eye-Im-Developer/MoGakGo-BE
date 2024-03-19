package io.oeid.mogakgo.domain.matching.application;

import static io.oeid.mogakgo.exception.code.ErrorCode403.MATCHING_FORBIDDEN_OPERATION;
import static io.oeid.mogakgo.exception.code.ErrorCode404.MATCHING_NOT_FOUND;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.chat.application.ChatService;
import io.oeid.mogakgo.domain.matching.domain.entity.Matching;
import io.oeid.mogakgo.domain.matching.domain.entity.enums.MatchingStatus;
import io.oeid.mogakgo.domain.matching.exception.MatchingException;
import io.oeid.mogakgo.domain.matching.infrastructure.MatchingJpaRepository;
import io.oeid.mogakgo.domain.matching.presentation.dto.MatchingHistoryRes;
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
    private final ChatService chatService;

    @Transactional
    public Long create(ProjectJoinRequest projectJoinRequest) {
        var project = projectJoinRequest.getProject();
        var creator = project.getCreator();
        var sender = projectJoinRequest.getSender();
        Matching matching = Matching.builder()
            .project(project)
            .sender(sender)
            .build();
        matchingJpaRepository.save(matching);
        chatService.createChatRoom(project, creator, sender);
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

    public CursorPaginationResult<MatchingHistoryRes> getMyMatches(
        Long tokenUserId, Long userId, MatchingStatus matchingStatus, CursorPaginationInfoReq cursorPaginationInfoReq
    ) {
        User tokenUser = userCommonService.getUserById(tokenUserId);
        // 본인만 매칭 기록 조회 가능
        if (!tokenUser.getId().equals(userId)) {
            throw new MatchingException(MATCHING_FORBIDDEN_OPERATION);
        }

        return matchingJpaRepository.getMyMatches(tokenUserId, matchingStatus, cursorPaginationInfoReq);
    }

    public Integer getDuplicateMatching(Long userId, Long participantId) {
        return matchingJpaRepository.findDuplicateMatching(userId, participantId);
    }

    public Integer getRegionCountByMatching(Long userId) {
        return matchingJpaRepository.findRegionCountByMatching(userId);
    }

    private Matching getMatching(Long matchingId) {
        return matchingJpaRepository.findById(matchingId)
            .orElseThrow(() -> new MatchingException(MATCHING_NOT_FOUND));
    }
}
