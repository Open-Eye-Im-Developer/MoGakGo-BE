package io.oeid.mogakgo.domain.user.service;

import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.domain.UserWantedJobTag;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import io.oeid.mogakgo.domain.user.infrastructure.UserWantedJobTagJpaRepository;
import io.oeid.mogakgo.domain.user.service.dto.req.UserSignUpRequest;
import io.oeid.mogakgo.domain.user.service.dto.res.UserSignUpResponse;
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
        User user = userRepository.findById(userSignUpRequest.getUserId())
            .orElseThrow(() -> new UserException(ErrorCode404.USER_NOT_FOUND));
        user.updateUsername(userSignUpRequest.getUsername());
        for (WantedJob wantedJob : userSignUpRequest.getWantedJobs()) {
            userWantedJobTagRepository.save(UserWantedJobTag.builder()
                .user(user)
                .wantedJob(wantedJob)
                .build());
        }
        return UserSignUpResponse.from(user);
    }
}
