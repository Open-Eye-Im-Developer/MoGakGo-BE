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
    @Schema(description = "언어 이미지 URL", example = "https://github.com/Open-Eye-Im-Developer/MoGakGo-BE/assets/85854384/6c518db0-1bda-4826-a505-250dbd8810e9")
    private final String imageUrl;

    public static UserDevelopLanguageApiRes from(UserDevelopLanguageRes response) {
        return new UserDevelopLanguageApiRes(response.getLanguage().getLanguage(),
            response.getByteSize(), response.getLanguage().getImageUrl());
    }
}
