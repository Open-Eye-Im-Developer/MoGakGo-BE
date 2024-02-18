package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode400 implements ErrorCode {
    PATH_PARAMETER_BAD_REQUEST("E000001", "잘못된 경로 파라미터입니다."),
    INVALID_INPUT_VALUE("E000002", "기본 유효성 검사에 실패하였습니다."),

    NOTIFICATION_TAG_NOT_NULL("E060001", "알림 태그는 필수값입니다."),
    NOTIFICATION_DETAIL_DATA_NOT_NULL("E060002", "알림 상세 데이터는 필수값입니다."),

    INVALID_PROJECT_MEETING_TIME("E030101", "프로젝트 만남 시간이 유효하지 않습니다."),
    INVALID_PROJECT_TAG_COUNT("E030102", "프로젝트 태그 갯수가 유효하지 않습니다. 1개 이상 3개 이하로 입력해야 합니다."),
    INVALID_PROJECT_TAG_CONTENT_LENGTH("E030103",
        "프로젝트 태그 내용 길이가 유효하지 않습니다. 1자 이상 7자 이하로 입력해야 합니다."),
    INVALID_PROJECT_NULL_DATA("E030104", "프로젝트를 생성하기 위해 null 이여서는 안되는 데이터가 null 입니다."),
    NOT_MATCH_MEET_LOCATION("E030105", "프로젝트 만남 장소가 유저가 동네인증 한 구역이 아닙니다."),
    PROJECT_DELETION_NOT_ALLOWED("E030106", "매칭 중이거나 대기중인 프로젝트는 삭제할 수 없습니다."),
    PROJECT_CANCEL_NOT_ALLOWED("E030107", "이미 취소 되었거나 종료된 프로젝트는 취소할 수 없습니다."),

    USER_DEVELOP_LANGUAGE_BAD_REQUEST("E020101", "개발 언어는 3개까지만 등록 가능합니다."),
    USER_WANTED_JOB_BAD_REQUEST("E020102", "희망 직무는 3개까지만 등록 가능합니다."),
    USERNAME_SHOULD_BE_NOT_EMPTY("E020103", "유저 이름은 비어있을 수 없습니다."),
    USER_REGION_SHOULD_BE_NOT_EMPTY("E020104", "유저 지역은 비어있을 수 없습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final String code;
    private final String message;

    ErrorCode400(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
