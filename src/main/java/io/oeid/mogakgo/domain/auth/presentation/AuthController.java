package io.oeid.mogakgo.domain.auth.presentation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {

    @GetMapping("/")
    public ResponseEntity<String> init(@AuthenticationPrincipal OAuth2User oAuth2User) {
        log.info(oAuth2User.toString());
        return ResponseEntity.ok(oAuth2User.toString());
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("http://127.0.0.1:8080/oauth2/authorization/github");
    }
}
