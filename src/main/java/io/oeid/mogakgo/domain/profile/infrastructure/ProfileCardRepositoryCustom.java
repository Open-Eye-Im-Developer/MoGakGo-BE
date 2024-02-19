package io.oeid.mogakgo.domain.profile.infrastructure;

import io.oeid.mogakgo.domain.geo.domain.enums.Region;
import io.oeid.mogakgo.domain.profile.domain.entity.ProfileCard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProfileCardRepositoryCustom {

    Slice<ProfileCard> findByCondition(
        Long cursorId, Long userId, Region region, Pageable pageable
    );
}
