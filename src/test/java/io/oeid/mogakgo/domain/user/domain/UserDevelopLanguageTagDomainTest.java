package io.oeid.mogakgo.domain.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import io.oeid.mogakgo.domain.user.domain.enums.DevelopLanguage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("도메인 테스트: UserDevelopLanguageTag")
class UserDevelopLanguageTagDomainTest {
    private static final Long GITHUB_PK = 85854384L;
    private static final String GITHUB_ID = "tidavid1";
    private static final String AVATAR_URL = "https://avatars.githubusercontent.com/u/85854384?v=4";
    private static final String GITHUB_URL = "https://github.com/tidavid1";
    private static final String REPOSITORY_URL = "https://api.github.com/users/tidavid1/repos";
    private static final User USER = User.of(GITHUB_PK, GITHUB_ID, AVATAR_URL, GITHUB_URL, REPOSITORY_URL);

    @Test
    void 개발언어_태그_생성() {
        // Arrange
        var expectedByteSize = 100;
        // Act
        UserDevelopLanguageTag userDevelopLanguageTag = UserDevelopLanguageTag.builder()
            .user(USER)
            .developLanguage(DevelopLanguage.JAVA)
            .byteSize(expectedByteSize)
            .build();

        // Assert
        assertThat(userDevelopLanguageTag)
            .extracting("user", "developLanguage", "byteSize")
            .containsExactly(USER, DevelopLanguage.JAVA, expectedByteSize);
    }
}