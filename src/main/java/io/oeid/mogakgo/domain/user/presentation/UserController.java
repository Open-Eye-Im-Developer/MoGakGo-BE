package io.oeid.mogakgo.domain.user.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.swagger.template.UserSwagger;
import io.oeid.mogakgo.domain.user.application.UserService;
import io.oeid.mogakgo.domain.user.presentation.dto.req.UserSignUpApiRequest;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserProfileCardApiResponse;
import io.oeid.mogakgo.domain.user.presentation.dto.res.UserSignUpApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController implements UserSwagger {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileCardApiResponse> userGetApi(@UserId Long userId) {
        var response = userService.getUserProfile(userId);
        return ResponseEntity.ok(UserProfileCardApiResponse.from(response));
    }

    @PatchMapping("/sign")
    public ResponseEntity<UserSignUpApiResponse> userSignUpApi(@UserId Long userId,
        @RequestBody @Valid UserSignUpApiRequest apiRequest) {
        var response = userService.userSignUp(apiRequest.toRequest(userId));
        return ResponseEntity.ok(UserSignUpApiResponse.from(response));
    }

    @DeleteMapping
    public ResponseEntity<Void> userDeleteApi(@UserId Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
