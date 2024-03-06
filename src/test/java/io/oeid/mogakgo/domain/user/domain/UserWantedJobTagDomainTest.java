package io.oeid.mogakgo.domain.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("도메인 테스트: UserDevelopLanguageTag")
class UserWantedJobTagDomainTest {

    private static final Long GITHUB_PK = 85854384L;
    private static final String GITHUB_ID = "tidavid1";
    private static final String AVATAR_URL = "https://avatars.githubusercontent.com/u/85854384?v=4";
    private static final String GITHUB_URL = "https://github.com/tidavid1";
    private static final String REPOSITORY_URL = "https://api.github.com/users/tidavid1/repos";
    private static final User USER = User.of(GITHUB_PK, GITHUB_ID, AVATAR_URL, GITHUB_URL, REPOSITORY_URL);

    @Test
    void 선호직종_태그_생성() {
        // Arrange
        var expectedWantedJob = WantedJob.BACKEND;
        // Act
        UserWantedJobTag userWantedJobTag = UserWantedJobTag.builder()
            .user(USER)
            .wantedJob(expectedWantedJob)
            .build();

        // Assert
        assertThat(userWantedJobTag)
            .extracting("user", "wantedJob")
            .containsExactly(USER, expectedWantedJob);
    }
}