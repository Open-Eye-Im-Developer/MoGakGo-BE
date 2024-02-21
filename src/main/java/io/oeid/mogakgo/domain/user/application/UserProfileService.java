package io.oeid.mogakgo.domain.user.application;

import io.oeid.mogakgo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserCommonService userCommonService;

    @Transactional
    public void decreaseAvailableLikeCount(Long userId) {
        User user = userCommonService.getUserById(userId);
        user.decreaseAvailableLikeCount();
    }

}
