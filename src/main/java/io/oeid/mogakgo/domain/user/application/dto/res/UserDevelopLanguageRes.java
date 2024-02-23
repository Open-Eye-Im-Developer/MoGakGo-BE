package io.oeid.mogakgo.domain.user.application.dto.res;

import io.oeid.mogakgo.domain.user.domain.UserDevelopLanguageTag;
import io.oeid.mogakgo.domain.user.domain.enums.DevelopLanguage;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDevelopLanguageRes {

    private final DevelopLanguage language;
    private final Integer byteSize;

    public static UserDevelopLanguageRes from(UserDevelopLanguageTag userDevelopLanguageTag) {
        return new UserDevelopLanguageRes(userDevelopLanguageTag.getDevelopLanguage(),
            userDevelopLanguageTag.getByteSize());
    }
}
