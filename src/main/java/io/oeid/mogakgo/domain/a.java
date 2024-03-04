package io.oeid.mogakgo.domain;

import io.oeid.mogakgo.domain.auth.jwt.JwtHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("test")
@RestController
public class a {

    private final JwtHelper jwtHelper;

    @GetMapping("/{id}")
    public String test(@PathVariable Long id) {
        String[] a = new String[1];
        a[0] = "ROLE_USER";
        return jwtHelper.sign(id, a).getAccessToken();
    }

}
