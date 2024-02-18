package io.oeid.mogakgo.domain.user.application;

import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.domain.UserWantedJobTag;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import io.oeid.mogakgo.domain.user.infrastructure.UserWantedJobTagJpaRepository;
import io.oeid.mogakgo.domain.user.application.dto.req.UserSignUpRequest;
import io.oeid.mogakgo.domain.user.application.dto.res.UserSignUpResponse;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserJpaRepository userRepository;
    private final UserWantedJobTagJpaRepository userWantedJobTagRepository;

    public UserSignUpResponse userSignUp(UserSignUpRequest userSignUpRequest) {
        User user = getUserById(userSignUpRequest.getUserId());
        user.updateUsername(userSignUpRequest.getUsername());
        for (WantedJob wantedJob : userSignUpRequest.getWantedJobs()) {
            userWantedJobTagRepository.save(UserWantedJobTag.builder()
                .user(user)
                .wantedJob(wantedJob)
                .build());
        }
        return UserSignUpResponse.from(user);
    }

    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        user.delete();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserException(ErrorCode404.USER_NOT_FOUND));
    }
}
