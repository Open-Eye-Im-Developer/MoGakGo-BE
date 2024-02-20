package io.oeid.mogakgo.domain.profile.application;

import static io.oeid.mogakgo.exception.code.ErrorCode400.PROFILE_CARD_LIKE_ALREADY_EXIST;
import static io.oeid.mogakgo.exception.code.ErrorCode403.PROFILE_CARD_LIKE_FORBIDDEN_OPERATION;

import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCardLike;
import io.oeid.mogakgo.domain.profile.exception.ProfileCardLikeException;
import io.oeid.mogakgo.domain.profile.infrastructure.ProfileCardLikeJpaRepository;
import io.oeid.mogakgo.domain.profile.presentation.dto.req.UserProfileLikeCreateAPIReq;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeInfoAPIRes;
import io.oeid.mogakgo.domain.user.application.UserCommonService;
import io.oeid.mogakgo.domain.user.application.UserProfileService;
import io.oeid.mogakgo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileCardLikeService {

    private final ProfileCardLikeJpaRepository profileCardLikeRepository;
    private final ProfileCardService profileCardService;
    private final UserCommonService userCommonService;
    private final UserProfileService userProfileService;

    @Transactional
    public Long create(Long userId, UserProfileLikeCreateAPIReq request) {
        User tokenUser = validateToken(userId);
        validateSendor(tokenUser, request.getSenderId());
        validateReceiver(userId);
        validateLikeAlreadyExist(request.getSenderId(), request.getReceiverId());

        User receiver = userCommonService.getUserById(request.getReceiverId());
        ProfileCardLike profileCardLike = request.toEntity(tokenUser, receiver);
        profileCardLikeRepository.save(profileCardLike);

        profileCardService.increaseTotalLikeAmount(receiver.getId());
        userProfileService.decreaseAvailableLikeCount(userId);

        return profileCardLike.getId();
    }

    // 나의 찔러보기 요청 수 조회
    public Long getReceivedLikeCountForProfileCard(Long userId, Long id) {
        User tokenUser = validateToken(userId);
        validateSendor(tokenUser, userId);

        return profileCardLikeRepository.getLikeCount(id);
    }

    // 내가 보낸 찔러보기 요청 수 조회
    public Long getSentLikeCountForProfileCard(Long userId, Long id) {
        User tokenUser = validateToken(userId);
        validateSendor(tokenUser, userId);

        return profileCardLikeRepository.getLikeCountByCondition(id, null);
    }

    public CursorPaginationResult<UserProfileLikeInfoAPIRes> getLikeInfoSenderProfile(
        Long userId, Long id, CursorPaginationInfoReq pageable) {
        User tokenUser = validateToken(userId);
        validateSendor(tokenUser, userId);

        return profileCardLikeRepository.getLikeInfoBySender(id, pageable);
    }

    private User validateToken(Long userId) {
        return userCommonService.getUserById(userId);
    }

    private void validateSendor(User tokenUser, Long userId) {
        if (!tokenUser.getId().equals(userId)) {
            throw new ProfileCardLikeException(PROFILE_CARD_LIKE_FORBIDDEN_OPERATION);
        }
    }

    private void validateReceiver(Long userId) {
        userCommonService.getUserById(userId);
    }

    private void validateLikeAlreadyExist(Long userId, Long creatorId) {
        if (profileCardLikeRepository.findBySenderAndReceiver(userId, creatorId).isPresent()) {
            throw new ProfileCardLikeException(PROFILE_CARD_LIKE_ALREADY_EXIST);
        }
    }
}
