package io.oeid.mogakgo.domain.user.application;

import io.oeid.mogakgo.domain.profile.application.ProfileCardService;
import io.oeid.mogakgo.domain.profile.application.dto.req.UserProfileCardReq;
import io.oeid.mogakgo.domain.user.application.dto.req.UserSignUpRequest;
import io.oeid.mogakgo.domain.user.application.dto.req.UserUpdateReq;
import io.oeid.mogakgo.domain.user.application.dto.res.UserDevelopLanguageRes;
import io.oeid.mogakgo.domain.user.application.dto.res.UserJandiRateRes;
import io.oeid.mogakgo.domain.user.application.dto.res.UserProfileResponse;
import io.oeid.mogakgo.domain.user.application.dto.res.UserSignUpResponse;
import io.oeid.mogakgo.domain.user.application.dto.res.UserUpdateRes;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserCommonService userCommonService;
    private final ProfileCardService profileCardService;
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
        profileCardService.create(UserProfileCardReq.of(user));
        return UserSignUpResponse.from(user);
    }

    @Transactional
    public List<UserDevelopLanguageRes> updateUserDevelopLanguages(long userId) {
        User user = userCommonService.getUserById(userId);
        user.deleteAllDevelopLanguageTags();
        var languageMap = userGithubUtil.updateUserDevelopLanguage(user.getRepositoryUrl());
        var languages = sortDevelopLanguageMap(languageMap);
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
    public UserUpdateRes updateUserInfos(Long userId, UserUpdateReq request) {
        User user = userCommonService.getUserById(userId);
        user.updateUserInfos(request.getUsername(), request.getAvatarUrl(), request.getBio());
        validateWantedJobDuplicate(request.getWantedJobs());
        for (WantedJob wantedJob : request.getWantedJobs()) {
            userWantedJobTagRepository.save(UserWantedJobTag.builder()
                .user(user)
                .wantedJob(wantedJob)
                .build());
        }
        return UserUpdateRes.from(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userCommonService.getUserById(userId);
        user.delete();
    }

    public UserJandiRateRes getUserJandiRate(Long userId) {
        User user = userCommonService.getUserById(userId);
        return UserJandiRateRes.of(user.getId(), user.getJandiRate());
    }

    private void validateWantedJobDuplicate(List<WantedJob> wantedJobs) {
        Set<WantedJob> wantedJobSet = new HashSet<>(wantedJobs);
        if (wantedJobSet.size() != wantedJobs.size()) {
            throw new UserException(ErrorCode400.USER_WANTED_JOB_DUPLICATE);
        }
    }

    private Map<String, Integer> sortDevelopLanguageMap(Map<String, Integer> languageMap){
        List<String> languageKeys = new ArrayList<>(languageMap.keySet());
        languageKeys.sort((o1, o2) -> languageMap.get(o2).compareTo(languageMap.get(o1)));
        Map<String, Integer> result = new LinkedHashMap<>();
        languageKeys.subList(0, 3).forEach(key -> result.put(key, languageMap.get(key)));
        return result;
    }

}
