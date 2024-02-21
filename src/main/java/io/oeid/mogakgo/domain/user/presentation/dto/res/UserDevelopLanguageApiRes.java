package io.oeid.mogakgo.domain.user.presentation.dto.res;

import io.oeid.mogakgo.domain.user.application.dto.res.UserDevelopLanguageRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "유저가 주요 개발 언어")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDevelopLanguageApiRes {

    @Schema(description = "개발 언어", example = "Java")
    private final String language;
    @Schema(description = "언어별 바이트 크기", example = "1000")
    private final Integer byteSize;

    public static UserDevelopLanguageApiRes from(UserDevelopLanguageRes response) {
        return new UserDevelopLanguageApiRes(response.getLanguage().getLanguage(),
            response.getByteSize());
    }
}
