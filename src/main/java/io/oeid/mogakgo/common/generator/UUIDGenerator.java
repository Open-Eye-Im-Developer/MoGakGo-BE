package io.oeid.mogakgo.common.generator;

import java.util.UUID;

public class UUIDGenerator {

    // TODO: unique한 값으로 생성할 수 있는 전략 고민 (토픽별? 타임스탬프 활용?)
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

}
