package io.oeid.mogakgo.domain.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.exception.UserException;
import io.oeid.mogakgo.domain.user.infrastructure.UserJpaRepository;
import io.oeid.mogakgo.exception.code.ErrorCode404;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("서비스 테스트: UserCommonService")
class UserCommonServiceTest {

    private static final User USER = User.of(85854384L,
        "tidavid1",
        "https://avatars.githubusercontent.com/u/85854384?v=4",
        "https://github.com/tidavid1",
        "https://api.github.com/users/tidavid1/repos");

    @InjectMocks
    private UserCommonService userCommonService;

    @Mock
    private UserJpaRepository userRepository;

    @Test
    void 유저_조회_성공() {
        // Arrange
        when(userRepository.findById(USER.getId())).thenReturn(Optional.of(USER));
        // Act
        var actualResult = userCommonService.getUserById(USER.getId());
        // Assert
        assertThat(actualResult).isEqualTo(USER);
    }

    @Test
    void 유저_조회_실패() {
        // Arrange
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> userCommonService.getUserById(3L))
            .isInstanceOf(UserException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode404.USER_NOT_FOUND);
    }
}