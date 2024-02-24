package io.oeid.mogakgo.domain.profile.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.PROFILE_CARD_LIKE_ALREADY_EXIST;
import static io.oeid.mogakgo.exception.code.ErrorCode400.PROFILE_CARD_LIKE_NOT_EXIST;
import static io.oeid.mogakgo.exception.code.ErrorCode403.PROFILE_CARD_LIKE_FORBIDDEN_OPERATION;
import static io.oeid.mogakgo.exception.code.ErrorCode404.PROFILE_CARD_NOT_FOUND;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCard;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCardLike;
import io.oeid.mogakgo.domain.profile.exception.ProfileCardLikeException;
import io.oeid.mogakgo.domain.profile.infrastructure.ProfileCardJpaRepository;
import io.oeid.mogakgo.domain.profile.infrastructure.ProfileCardLikeJpaRepository;
import io.oeid.mogakgo.domain.profile.presentation.dto.req.UserProfileLikeCancelAPIReq;
import io.oeid.mogakgo.domain.profile.presentation.dto.req.UserProfileLikeCreateAPIReq;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeInfoAPIRes;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileCardLikeService {

    private final ProfileCardLikeJpaRepository profileCardLikeRepository;
    private final ProfileCardJpaRepository profileCardRepository;
    private final UserCommonService userCommonService;

    @Transactional
    public Long create(Long userId, UserProfileLikeCreateAPIReq request) {
        User user = validateToken(userId);

        ProfileCard profileCard = validateProfileCardExist(request.getReceiverId());

        if (hasAlreadyExist(userId, request.getReceiverId())) {
            throw new ProfileCardLikeException(PROFILE_CARD_LIKE_ALREADY_EXIST);
        }

        profileCard.increaseLikeAmount();
        user.decreaseAvailableLikeCount();

        // 프로필 카드에 '찔러보기' 요청 생성
        ProfileCardLike profileCardLike = request.toEntity(user, profileCard.getUser());
        profileCardLikeRepository.save(profileCardLike);

        return profileCardLike.getId();
    }

    @Transactional
    public void cancel(Long userId, UserProfileLikeCancelAPIReq request) {
        User user = validateToken(userId);
        validateSender(user, request.getSenderId());

        ProfileCard profileCard = validateProfileCardExist(request.getReceiverId());

        if (!hasAlreadyExist(userId, request.getReceiverId())) {
            throw new ProfileCardLikeException(PROFILE_CARD_LIKE_NOT_EXIST);
        }

        profileCard.decreaseLikeAmount();
        user.increaseAvailableLikeCount();

        // 프로필 카드에 '찔러보기' 요청 취소
        ProfileCardLike profileCardLike = getBySenderAndReceiver(userId, request.getReceiverId());
        profileCardLikeRepository.delete(profileCardLike);
    }

    // 나의 찔러보기 요청 수 조회
    public Long getReceivedLikeCountForProfileCard(Long userId, Long id) {
        User tokenUser = validateToken(userId);
        validateSender(tokenUser, userId);

        return profileCardLikeRepository.getReceivedLikeCount(id);
    }

    // 내가 보낸 찔러보기 요청 수 조회
    public Long getSentLikeCountForProfileCard(Long userId, Long id) {
        User tokenUser = validateToken(userId);
        validateSender(tokenUser, userId);

        return profileCardLikeRepository.getLikeCountByCondition(id, null);
    }

    public CursorPaginationResult<UserProfileLikeInfoAPIRes> getLikeInfoSenderProfile(
        Long userId, Long id, CursorPaginationInfoReq pageable) {
        User tokenUser = validateToken(userId);
        validateSender(tokenUser, userId);

        return profileCardLikeRepository.getLikeInfoBySender(id, pageable);
    }

    private ProfileCardLike getBySenderAndReceiver(Long senderId, Long receiverId) {
        return profileCardLikeRepository.findBySenderAndReceiver(senderId, receiverId)
            .orElseThrow(() -> new ProfileCardLikeException(PROFILE_CARD_LIKE_NOT_EXIST));
    }

    private User validateToken(Long userId) {
        return userCommonService.getUserById(userId);
    }

    private void validateSender(User tokenUser, Long userId) {
        if (!tokenUser.getId().equals(userId)) {
            throw new ProfileCardLikeException(PROFILE_CARD_LIKE_FORBIDDEN_OPERATION);
        }
    }

    private ProfileCard validateProfileCardExist(Long receiverId) {
        return profileCardRepository.findByUserId(receiverId)
            .orElseThrow(() -> new ProfileCardLikeException(PROFILE_CARD_NOT_FOUND));
    }

    private boolean hasAlreadyExist(Long userId, Long creatorId) {
        return profileCardLikeRepository.findBySenderAndReceiver(userId, creatorId).isPresent();
    }
}
