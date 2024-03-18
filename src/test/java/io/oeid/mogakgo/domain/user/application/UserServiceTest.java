package io.oeid.mogakgo.domain.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.oeid.mogakgo.domain.achievement.infrastructure.AchievementJpaRepository;
import io.oeid.mogakgo.domain.achievement.infrastructure.UserAchievementJpaRepository;
import io.oeid.mogakgo.domain.profile.application.ProfileCardService;
import io.oeid.mogakgo.domain.user.application.dto.req.UserSignUpRequest;
import io.oeid.mogakgo.domain.user.application.dto.req.UserUpdateReq;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.domain.enums.DevelopLanguage;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.infrastructure.UserDevelopLanguageTagJpaRepository;
import io.oeid.mogakgo.domain.user.infrastructure.UserWantedJobTagJpaRepository;
import io.oeid.mogakgo.domain.user.presentation.dto.req.UserUpdateApiReq;
import io.oeid.mogakgo.domain.user.util.UserGithubUtil;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("서비스 테스트: UserService")
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserCommonService userCommonService;
    @Mock
    private UserWantedJobTagJpaRepository userWantedJobTagRepository;
    @Mock
    private UserDevelopLanguageTagJpaRepository userDevelopLanguageTagRepository;
    @Mock
    private ProfileCardService profileCardService;
    @Mock
    private UserGithubUtil userGithubUtil;
    @Mock
    private AchievementJpaRepository achievementRepository;
    @Mock
    private UserAchievementJpaRepository userAchievementRepository;
    private static User user;

    @BeforeEach
    void init() {
        user = User.of(85854384L,
            "tidavid1",
            "https://avatars.githubusercontent.com/u/85854384?v=4",
            "https://github.com/tidavid1",
            "https://api.github.com/users/tidavid1/repos");
    }

    @Test
    void 유저_회원가입_성공() {
        // Arrange
        var expectedUserId = 1L;
        var expectedUsername = "tidavid1";
        var expectedWantedJobs = List.of(WantedJob.BACKEND, WantedJob.DEVOPS);
        var userSignUpRequest = new UserSignUpRequest(expectedUserId, expectedUsername,
            expectedWantedJobs);
        when(userCommonService.getUserById(expectedUserId)).thenReturn(user);
        // Act
        var actualResult = userService.userSignUp(userSignUpRequest);
        // Assert
        assertThat(actualResult)
            .isNotNull()
            .hasFieldOrProperty("userId")
            .hasFieldOrProperty("githubId")
            .hasFieldOrProperty("avatarUrl")
            .hasFieldOrProperty("githubUrl")
            .extracting("username", "wantedJobs")
            .containsExactly(expectedUsername, expectedWantedJobs);
        assertThat(user.getSignupYn()).isTrue();
    }

    @Test
    void 유저_회원가입_실패_중복된_선호직업_제공시() {
        // Arrange
        var expectedUserId = 1L;
        var expectedUsername = "tidavid1";
        var expectedWantedJobs = List.of(WantedJob.BACKEND, WantedJob.BACKEND);
        var userSignUpRequest = new UserSignUpRequest(expectedUserId, expectedUsername,
            expectedWantedJobs);
        when(userCommonService.getUserById(expectedUserId)).thenReturn(user);
        // Act & Assert
        assertThatThrownBy(() -> userService.userSignUp(userSignUpRequest))
            .isInstanceOf(UserException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode400.USER_WANTED_JOB_DUPLICATE);
        // Assert
    }

    @Test
    void 유저_삭제_성공() {
        // Arrange
        var expectedUserId = 1L;
        when(userCommonService.getUserById(expectedUserId)).thenReturn(user);
        // Act
        userService.deleteUser(expectedUserId);
        // Assert
        assertThat(user.getDeletedAt()).isNotNull();
    }

    @Test
    void 유저_개발언어_업데이트() {
        // Arrange
        var expectedUserId = 1L;
        when(userCommonService.getUserById(expectedUserId)).thenReturn(user);
        when(userGithubUtil.updateUserDevelopLanguage(any(String.class))).thenReturn(Map.of(
            "Java", 100,
            "JavaScript", 200,
            "Python", 300
        ));
        // Act
        var actualResult = userService.updateUserDevelopLanguages(expectedUserId);
        // Assert
        assertThat(actualResult)
            .isNotNull()
            .extracting("language")
            .contains(DevelopLanguage.JAVA, DevelopLanguage.JAVASCRIPT, DevelopLanguage.PYTHON);
        assertThat(actualResult)
            .extracting("byteSize")
            .contains(300, 200, 100);
    }

    @Test
    void 유저_프로필_조회() {
        // Arrange
        var expectedUserId = 1L;
        when(userCommonService.getUserById(expectedUserId)).thenReturn(user);
        // Act
        var actualResult = userService.getUserProfile(expectedUserId);
        // Assert
        assertThat(actualResult)
            .isNotNull()
            .hasFieldOrProperty("id")
            .hasFieldOrPropertyWithValue("username", "tidavid1")
            .hasFieldOrPropertyWithValue("githubId", "tidavid1")
            .hasFieldOrPropertyWithValue("avatarUrl",
                "https://avatars.githubusercontent.com/u/85854384?v=4")
            .hasFieldOrPropertyWithValue("githubUrl", "https://github.com/tidavid1")
            .hasFieldOrPropertyWithValue("bio", null)
            .hasFieldOrPropertyWithValue("jandiRate", 10.0)
            .hasFieldOrProperty("developLanguages")
            .hasFieldOrProperty("wantedJobs");
    }

    @Test
    void 유저_정보_업데이트_성공() {
        // Arrange
        var expectedUserId = 1L;
        var expectedUsername = "tidavid2";
        var expectedBio = "new bio";
        var expectedAvatarUrl = "https://avatars.githubusercontent.com/u/85854384?v=4";
        var userUpdateReq = UserUpdateReq.from(new UserUpdateApiReq(
            expectedUsername,
            expectedBio,
            expectedAvatarUrl,
            List.of("BACKEND", "DEVOPS")
        ));
        when(userCommonService.getUserById(expectedUserId)).thenReturn(user);
        // Act
        var actualResult = userService.updateUserInfos(expectedUserId, userUpdateReq);
        // Assert
        assertThat(actualResult)
            .isNotNull()
            .hasFieldOrPropertyWithValue("username", expectedUsername)
            .hasFieldOrPropertyWithValue("bio", expectedBio)
            .hasFieldOrPropertyWithValue("avatarUrl", expectedAvatarUrl);
        assertThat(actualResult.getWantedJobs())
            .hasSize(2)
            .contains(WantedJob.BACKEND, WantedJob.DEVOPS);
    }

    @Test
    void 유저_잔디력_조회(){
        // Arrange
        var expectedUserId = 1L;
        when(userCommonService.getUserById(expectedUserId)).thenReturn(user);
        // Act
        var actualResult = userService.getUserJandiRate(expectedUserId);
        // Assert
        assertThat(actualResult)
            .hasFieldOrProperty("userId")
            .hasFieldOrPropertyWithValue("jandiRate", 10.0);
    }

}