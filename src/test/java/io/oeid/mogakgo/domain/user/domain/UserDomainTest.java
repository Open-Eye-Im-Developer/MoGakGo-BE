package io.oeid.mogakgo.domain.user.domain;

import static io.oeid.mogakgo.domain.user.domain.enums.Role.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.review.domain.enums.ReviewRating;
import io.oeid.mogakgo.domain.user.domain.enums.DevelopLanguage;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.exception.code.ErrorCode400;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("도메인 테스트: User")
class UserDomainTest {

    private static final Long GITHUB_PK = 85854384L;
    private static final String GITHUB_ID = "tidavid1";
    private static final String AVATAR_URL = "https://avatars.githubusercontent.com/u/85854384?v=4";
    private static final String GITHUB_URL = "https://github.com/tidavid1";
    private static final String REPOSITORY_URL = "https://api.github.com/users/tidavid1/repos";

    private static User user;

    @BeforeEach
    void init() {
        user = User.of(GITHUB_PK, GITHUB_ID, AVATAR_URL, GITHUB_URL, REPOSITORY_URL);
    }

    @Test
    void 유저_생성() {
        // Act
        var actualResult = User.of(GITHUB_PK, GITHUB_ID, AVATAR_URL, GITHUB_URL, REPOSITORY_URL);
        // Assert
        assertThat(actualResult)
            .hasFieldOrPropertyWithValue("githubPk", GITHUB_PK)
            .hasFieldOrPropertyWithValue("username", GITHUB_ID)
            .hasFieldOrPropertyWithValue("githubId", GITHUB_ID)
            .hasFieldOrPropertyWithValue("avatarUrl", AVATAR_URL)
            .hasFieldOrPropertyWithValue("githubUrl", GITHUB_URL)
            .hasFieldOrPropertyWithValue("repositoryUrl", REPOSITORY_URL)
            .hasFieldOrPropertyWithValue("role", ROLE_USER)
            .hasFieldOrPropertyWithValue("jandiRate", 10d)
            .hasFieldOrPropertyWithValue("signupYn", false)
            .isNotNull();
    }

    @Test
    void 인가_정보_확인() {
        // Act
        var actualResult = user.getAuthorities();
        // Assert
        assertThat(actualResult)
            .hasSize(1)
            .allMatch(authority -> authority.getAuthority().equals(ROLE_USER.name()));
    }

    @Test
    void Github_정보_업데이트() {
        // Arrange
        var newGithubId = "newGithubId";
        var newAvatarUrl = "newAvatarUrl";
        var newGithubUrl = "newGithubUrl";
        var newRepositoryUrl = "newRepositoryUrl";
        // Act
        user.updateGithubInfo(newGithubId, newAvatarUrl, newGithubUrl, newRepositoryUrl);
        // Assert
        assertThat(user)
            .hasFieldOrPropertyWithValue("githubId", newGithubId)
            .hasFieldOrPropertyWithValue("avatarUrl", newAvatarUrl)
            .hasFieldOrPropertyWithValue("githubUrl", newGithubUrl)
            .hasFieldOrPropertyWithValue("repositoryUrl", newRepositoryUrl);
    }

    @Test
    void 유저_이름_업데이트_성공() {
        // Arrange
        var newUsername = "newUsername";
        // Act
        user.updateUsername(newUsername);
        // Assert
        assertThat(user)
            .hasFieldOrPropertyWithValue("username", newUsername);
    }

    @ParameterizedTest
    @MethodSource("invalidUsername")
    void 유저_이름_업데이트_실패_이름이_유효하지_않음(String newUsername) {
        // Act & Assert
        assertThatThrownBy(() -> user.updateUsername(newUsername))
            .isInstanceOf(UserException.class)
            .hasFieldOrPropertyWithValue(
                "errorCode", ErrorCode400.USERNAME_SHOULD_BE_NOT_EMPTY);
    }

    @Test
    void 좋아요_가능_개수_증가() {
        // Act
        user.increaseAvailableLikeCount();
        // Assert
        assertThat(user)
            .hasFieldOrPropertyWithValue("availableLikeCount", 1);
    }

    @Test
    void 좋아요_가능_개수_감소_성공() {
        // Arrange
        user.increaseAvailableLikeCount();
        // Act
        user.decreaseAvailableLikeCount();
        // Assert
        assertThat(user)
            .hasFieldOrPropertyWithValue("availableLikeCount", 0);
    }

    @Test
    void 좋아요_가능_개수_감소_실패_좋아요_가능_개수가_0일_때() {
        // Act & Assert
        assertThatThrownBy(() -> user.decreaseAvailableLikeCount())
            .isInstanceOf(UserException.class)
            .hasFieldOrPropertyWithValue(
                "errorCode", ErrorCode400.USER_AVAILABLE_LIKE_COUNT_IS_ZERO);
    }

    @Test
    void 유저_삭제() {
        // Act
        user.delete();
        // Assert
        assertThat(user)
            .hasFieldOrProperty("deletedAt").isNotNull();
    }

    @Test
    void 유저_회원_가입_완료() {
        // Act
        user.signUpComplete();
        // Assert
        assertThat(user)
            .hasFieldOrPropertyWithValue("signupYn", true);
    }

    @Test
    void 유저_회원_가입_실패_이미_유저가_회원가입을_완료한_경우() {
        // Arrange
        user.signUpComplete();
        // Act
        assertThatThrownBy(() -> user.signUpComplete())
            .isInstanceOf(UserException.class)
            .hasFieldOrPropertyWithValue(
                "errorCode", ErrorCode400.USER_ALREADY_SIGNUP
            );
    }

    @Test
    void 유저_정보_업데이트_성공() {
        // Arrange
        var newUsername = "newUsername";
        var newAvatarUrl = "newAvatarUrl";
        var newBio = "newBio";
        // Act
        user.updateUserInfos(newUsername, newAvatarUrl, newBio);
        // Assert
        assertThat(user)
            .hasFieldOrPropertyWithValue("username", newUsername)
            .hasFieldOrPropertyWithValue("avatarUrl", newAvatarUrl)
            .hasFieldOrPropertyWithValue("bio", newBio);
    }

    @ParameterizedTest
    @MethodSource("invalidAvatarUrl")
    void 유저_정보_업데이트_실패_아바타_주소가_유효하지_않음(String newUsername, String newAvatarUrl, String newBio) {
        // Act & Assert
        assertThatThrownBy(() -> user.updateUserInfos(newUsername, newAvatarUrl, newBio))
            .isInstanceOf(UserException.class)
            .hasFieldOrPropertyWithValue(
                "errorCode", ErrorCode400.USER_AVATAR_URL_NOT_NULL);
    }

    @ParameterizedTest
    @MethodSource("jandiRateByReview")
    void 유저_잔디력_업데이트_by_리뷰(ReviewRating rating, double time, double expectedRate) {
        // Arrange
        User user = User.of(GITHUB_PK, GITHUB_ID, AVATAR_URL, GITHUB_URL, REPOSITORY_URL);
        // Act
        user.updateJandiRateByReview(rating, time);
        // Assert
        assertThat(user)
            .hasFieldOrPropertyWithValue("jandiRate", expectedRate);
    }

    @Test
    void 유저_잔디력_업데이트_by_취소() {
        // Arrange
        User user = User.of(GITHUB_PK, GITHUB_ID, AVATAR_URL, GITHUB_URL, REPOSITORY_URL);
        // Assert
        var expectedJandiRate = user.getJandiRate() - 1 * 2.5;
        // Act
        user.updateJandiRateByCancel();
        // Assert
        assertThat(user)
            .hasFieldOrPropertyWithValue("jandiRate", expectedJandiRate);
    }

    @Test
    void 유저_개발언어_태그_추가() {
        // Act
        List<UserDevelopLanguageTag> expectedUserDevelopLanguageTags = List.of(
            UserDevelopLanguageTag.builder()
                .developLanguage(DevelopLanguage.JAVA)
                .user(user)
                .byteSize(10000)
                .build(),
            UserDevelopLanguageTag.builder()
                .developLanguage(DevelopLanguage.KOTLIN)
                .user(user)
                .byteSize(5000)
                .build(),
            UserDevelopLanguageTag.builder()
                .developLanguage(DevelopLanguage.JAVASCRIPT)
                .user(user)
                .byteSize(3000)
                .build()
        );
        // Assert
        assertThat(user.getUserDevelopLanguageTags())
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(expectedUserDevelopLanguageTags);
    }

    @Test
    void 유저_개발언어_태크_추가_실패() {
        // Arrange
        UserDevelopLanguageTag.builder().developLanguage(DevelopLanguage.JAVA).user(user).byteSize(10000).build();
        UserDevelopLanguageTag.builder().developLanguage(DevelopLanguage.KOTLIN).user(user).byteSize(5000).build();
        UserDevelopLanguageTag.builder().developLanguage(DevelopLanguage.JAVASCRIPT).user(user).byteSize(3000).build();
        // Act & Assert
        assertThatThrownBy(
            () -> UserDevelopLanguageTag.builder().developLanguage(DevelopLanguage.HTML).user(user)
                .byteSize(300).build())
            .isInstanceOf(UserException.class)
            .hasFieldOrPropertyWithValue(
                "errorCode", ErrorCode400.USER_DEVELOP_LANGUAGE_BAD_REQUEST);
    }

    @Test
    void 유저_개발언어_제거(){
        // Arrange
        UserDevelopLanguageTag.builder().developLanguage(DevelopLanguage.JAVA).user(user).byteSize(10000).build();
        UserDevelopLanguageTag.builder().developLanguage(DevelopLanguage.KOTLIN).user(user).byteSize(5000).build();
        UserDevelopLanguageTag.builder().developLanguage(DevelopLanguage.JAVASCRIPT).user(user).byteSize(3000).build();
        // Act
        user.deleteAllDevelopLanguageTags();
        // Assert
        assertThat(user.getUserDevelopLanguageTags()).isEmpty();
    }

    @Test
    void 유저_선호직종_추가_성공(){
        // Act
        List<UserWantedJobTag> expectedUserWantedJobTags = List.of(
            UserWantedJobTag.builder()
                .wantedJob(WantedJob.BACKEND)
                .user(user)
                .build(),
            UserWantedJobTag.builder()
                .wantedJob(WantedJob.DBA)
                .user(user)
                .build(),
            UserWantedJobTag.builder()
                .wantedJob(WantedJob.DEVOPS)
                .user(user)
                .build()
        );
        // Assert
        assertThat(user.getUserWantedJobTags())
            .hasSize(3)
            .containsExactlyInAnyOrderElementsOf(expectedUserWantedJobTags);
    }

    @Test
    void 유저_선호직종_추가_실패(){
        // Arrange
        UserWantedJobTag.builder().wantedJob(WantedJob.BACKEND).user(user).build();
        UserWantedJobTag.builder().wantedJob(WantedJob.DBA).user(user).build();
        UserWantedJobTag.builder().wantedJob(WantedJob.DEVOPS).user(user).build();
        // Act & Assert
        assertThatThrownBy(
            () -> UserWantedJobTag.builder().wantedJob(WantedJob.FRONTEND).user(user).build())
            .isInstanceOf(UserException.class)
            .hasFieldOrPropertyWithValue(
                "errorCode", ErrorCode400.USER_DEVELOP_LANGUAGE_BAD_REQUEST);
    }

    @Test
    void 유저_선호직종_제거(){
        // Arrange
        UserWantedJobTag.builder().wantedJob(WantedJob.BACKEND).user(user).build();
        UserWantedJobTag.builder().wantedJob(WantedJob.DBA).user(user).build();
        UserWantedJobTag.builder().wantedJob(WantedJob.DEVOPS).user(user).build();
        // Act
        user.deleteAllWantJobTags();
        // Assert
        assertThat(user.getUserWantedJobTags()).isEmpty();
    }

    @Test
    void 유저_지역_업데이트_성공(){
        // Arrange
        var expectedRegion = Region.NOWON;
        // Act
        user.updateRegion(expectedRegion);
        // Assert
        assertThat(user)
            .hasFieldOrPropertyWithValue("region", expectedRegion)
            .hasFieldOrProperty("regionAuthenticationAt").isNotNull();
    }

    @Test
    void 유저_지역_업데이트_실패_null인_경우(){
        // Act & Assert
        assertThatThrownBy(() -> user.updateRegion(null))
            .isInstanceOf(UserException.class)
            .hasFieldOrPropertyWithValue(
                "errorCode", ErrorCode400.USER_REGION_SHOULD_BE_NOT_EMPTY);
    }

    private static Stream<Arguments> invalidUsername() {
        return Stream.of(
            Arguments.of(""),
            null
        );
    }

    private static Stream<Arguments> invalidAvatarUrl() {
        return Stream.of(
            Arguments.of("newUsername", "", "newBio"),
            Arguments.of("newUsername", null, "newBio")
        );
    }

    private static Stream<Arguments> jandiRateByReview() {
        return Stream.of(
            Arguments.of(ReviewRating.ONE, 1.0, 5),
            Arguments.of(ReviewRating.TWO, 1.0, 7.5),
            Arguments.of(ReviewRating.THREE, 1.0, 12.5),
            Arguments.of(ReviewRating.FOUR, 1.0, 15),
            Arguments.of(ReviewRating.FIVE, 1.0, 17.5)
        );
    }


}