package io.oeid.mogakgo.domain.achievement.domain.entity.enums;

import java.util.List;
import lombok.Getter;

@Getter
public enum ActivityType {

    FROM_ONE_STEP(List.of(2, 3, 4)),
    GOOD_PERSON_GOOD_MEETUP(List.of(5, 6, 7)),
    LIKE_E(List.of(8, 9, 10)),
    MY_DESTINY(List.of(11)),
    CAPTURE_FAIL_EXIST(List.of(12)),
    RUN_AWAY_FROM_MONSTAR_BALL(List.of(13)),
    PLEASE_GIVE_ME_MOGAK(List.of(14, 15, 16)),
    BRAVE_EXPLORER(List.of(17)),
    NOMAD_CODER(List.of(18)),
    CATCH_ME_IF_YOU_CAN(List.of(19, 20, 21)),
    LEAVE_YOUR_MARK(List.of(22)),
    WHAT_A_POPULAR_PERSON(List.of(23, 24, 25)),
    CONTACT_WITH_GOD(List.of(26)),
    FRESH_DEVELOPER(List.of(28, 29, 30)),
    ;

    private final List<Integer> includedDbIdList;

    ActivityType(List<Integer> includedDbIdList) {
        this.includedDbIdList = includedDbIdList;
    }

}
