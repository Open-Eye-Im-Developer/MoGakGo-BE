package io.oeid.mogakgo.domain.user.application;

import io.oeid.mogakgo.domain.user.application.dto.req.UserSignUpRequest;
import io.oeid.mogakgo.domain.user.application.dto.res.UserDevelopLanguageRes;
import io.oeid.mogakgo.domain.user.application.dto.res.UserProfileResponse;
import io.oeid.mogakgo.domain.user.application.dto.res.UserSignUpResponse;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.domain.UserDevelopLanguageTag;
import io.oeid.mogakgo.domain.user.domain.UserWantedJobTag;
import io.oeid.mogakgo.domain.user.domain.enums.DevelopLanguage;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.infrastructure.UserDevelopLanguageTagJpaRepository;
import io.oeid.mogakgo.domain.user.infrastructure.UserWantedJobTagJpaRepository;
import io.oeid.mogakgo.domain.user.util.UserGithubUtil;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserCommonService userCommonService;
    private final UserWantedJobTagJpaRepository userWantedJobTagRepository;
    private final UserDevelopLanguageTagJpaRepository userDevelopLanguageTagRepository;
    private final UserGithubUtil userGithubUtil;

    @Transactional
    public UserSignUpResponse userSignUp(UserSignUpRequest userSignUpRequest) {
        User user = userCommonService.getUserById(userSignUpRequest.getUserId());
        user.updateUsername(userSignUpRequest.getUsername());
        validateWantedJobDuplicate(userSignUpRequest.getWantedJobs());
        for (WantedJob wantedJob : userSignUpRequest.getWantedJobs()) {
            userWantedJobTagRepository.save(UserWantedJobTag.builder()
                .user(user)
                .wantedJob(wantedJob)
                .build());
        }
        user.signUpComplete();
        return UserSignUpResponse.from(user);
    }

    @Transactional
    public List<UserDevelopLanguageRes> updateUserDevelopLanguages(long userId) {
        User user = userCommonService.getUserById(userId);
        user.deleteAllDevelopLanguageTags();
        var languages = userGithubUtil.updateUserDevelopLanguage(user.getRepositoryUrl());
        languages.forEach((key, value) -> {
            UserDevelopLanguageTag developLanguageTag = UserDevelopLanguageTag.builder()
                .user(user)
                .developLanguage(DevelopLanguage.of(key))
                .byteSize(value)
                .build();
            userDevelopLanguageTagRepository.save(developLanguageTag);
        });
        List<UserDevelopLanguageRes> response = new ArrayList<>();
        for (var developLanguage : user.getUserDevelopLanguageTags()) {
            response.add(UserDevelopLanguageRes.from(developLanguage));
        }
        return response;
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userCommonService.getUserById(userId);
        return UserProfileResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userCommonService.getUserById(userId);
        user.delete();
    }

    private void validateWantedJobDuplicate(List<WantedJob> wantedJobs) {
        Set<WantedJob> wantedJobSet = new HashSet<>(wantedJobs);
        if (wantedJobSet.size() != wantedJobs.size()) {
            throw new UserException(ErrorCode400.USER_WANTED_JOB_DUPLICATE);
        }
    }

    private void validateDevelopLanguageDuplicate(List<DevelopLanguage> developLanguages) {
        Set<DevelopLanguage> developLanguageSet = new HashSet<>(developLanguages);
        if (developLanguageSet.size() != developLanguages.size()) {
            throw new UserException(ErrorCode400.USER_DEVELOP_LANGUAGE_DUPLICATE);
        }
    }

}
