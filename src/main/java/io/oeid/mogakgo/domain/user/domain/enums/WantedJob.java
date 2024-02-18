package io.oeid.mogakgo.domain.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WantedJob {
    BACKEND("서버/백엔드"),
    FRONTEND("프론트엔드"),
    FULLSTACK("풀스택"),
    ANDROID("안드로이드"),
    IOS("iOS"),
    MACHINE_LEARNING("머신러닝"),
    ARTIFICIAL_INTELLIGENCE("인공지능(AI)"),
    DATA_ENGINEER("데이터 엔지니어링"),
    DBA("DBA"),
    MOBILE_GAME("모바일 게임"),
    SYSTEM_NETWORK("시스템/네트워크"),
    SYSTEM_SW("시스템 소프트웨어"),
    DEVOPS("데브옵스"),
    INTERNET_SECURITY("인터넷 보안"),
    EMBEDDED_SOFTWARE("임베디드 소프트웨어"),
    ROBOTICS_MIDDLEWARE("로보틱스 미들웨어"),
    QA("QA"),
    IOT("IoT"),
    APPLICATION_SW("응용 소프트웨어"),
    BLOCKCHAIN("블록체인"),
    PROJECT_MANAGEMENT("PM"),
    WEB_PUBLISHING("웹 퍼블리싱"),
    CROSS_PLATFORM("크로스 플랫폼"),
    VR_AR_3D("VR/AR/3D"),
    ERP("ERP"),
    GRAPHICS("그래픽스");
    private final String jobName;
}
