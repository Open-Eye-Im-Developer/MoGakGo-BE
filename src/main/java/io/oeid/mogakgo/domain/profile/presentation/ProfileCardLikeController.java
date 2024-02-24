package io.oeid.mogakgo.domain.profile.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.base.CursorPaginationInfoReq;
import io.oeid.mogakgo.common.base.CursorPaginationResult;
import io.oeid.mogakgo.common.swagger.template.ProfileCardLikeSwagger;
import io.oeid.mogakgo.domain.profile.application.ProfileCardLikeService;
import io.oeid.mogakgo.domain.profile.presentation.dto.req.UserProfileLikeCancelAPIReq;
import io.oeid.mogakgo.domain.profile.presentation.dto.req.UserProfileLikeCreateAPIReq;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeAPIRes;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeCreateAPIRes;
import io.oeid.mogakgo.domain.profile.presentation.dto.res.UserProfileLikeInfoAPIRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileCardLikeController implements ProfileCardLikeSwagger {

    private final ProfileCardLikeService profileCardLikeService;

    @PostMapping("/like")
    public ResponseEntity<UserProfileLikeCreateAPIRes> create(
        @UserId Long userId, @Valid @RequestBody UserProfileLikeCreateAPIReq request
    ) {
        return ResponseEntity.status(201)
            .body(UserProfileLikeCreateAPIRes.of(profileCardLikeService.create(userId, request)));
    }

    // 사용자가 받은 '찔러보기' 요청 수 조회 API
    @GetMapping("/{id}/receive/like")
    public ResponseEntity<UserProfileLikeAPIRes> getProfileLikeCountByReceiver(
        @UserId Long userId, @PathVariable Long id
    ) {
        Long likeCount = profileCardLikeService.getReceivedLikeCountForProfileCard(userId, id);
        return ResponseEntity.ok().body(UserProfileLikeAPIRes.from(id, likeCount));
    }

    // 사용자가 보낸 '찔러보기' 요청 수 조회 API
    @GetMapping("/{id}/send/like")
    public ResponseEntity<UserProfileLikeAPIRes> getProfileLikeCountBySender(
        @UserId Long userId, @PathVariable Long id
    ) {
        Long likeCount = profileCardLikeService.getSentLikeCountForProfileCard(userId, id);
        return ResponseEntity.ok().body(UserProfileLikeAPIRes.from(id, likeCount));
    }

    // 사용자가 보낸 '찔러보기' 요청 상세 조회 API
    @GetMapping("/list/{id}/like")
    public ResponseEntity<CursorPaginationResult<UserProfileLikeInfoAPIRes>> getProfileLikeInfoBySender(
        @UserId Long userId, @PathVariable Long id,
        @Valid @ModelAttribute CursorPaginationInfoReq pageable
    ) {
        return ResponseEntity.ok()
            .body(profileCardLikeService.getLikeInfoSenderProfile(userId, id, pageable));
    }

    @DeleteMapping("/like")
    public ResponseEntity<Void> cancel(
        @UserId Long userId, @Valid @RequestBody UserProfileLikeCancelAPIReq request
    ) {
        profileCardLikeService.cancel(userId, request);
        return ResponseEntity.noContent().build();
    }
}
