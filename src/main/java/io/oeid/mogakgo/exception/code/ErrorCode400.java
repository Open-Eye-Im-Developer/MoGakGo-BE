package io.oeid.mogakgo.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode400 implements ErrorCode {
    PATH_PARAMETER_BAD_REQUEST("E000001", "잘못된 경로 파라미터입니다."),
    INVALID_INPUT_VALUE("E000002", "기본 유효성 검사에 실패하였습니다."),

    NOTIFICATION_TAG_SHOULD_LIKE_MATCHING("E060001", "매칭 관련 알림 태그를 사용해야합니다."),
    NOTIFICATION_USER_NOT_NULL("E060002", "알림을 받는 유저 정보는 필수입니다."),
    NOTIFICATION_FCM_TOKEN_NOT_NULL("E060003", "FCM 토큰은 필수값입니다."),
    NOTIFICATION_RECEIVER_NOT_NULL("E060004", "알림을 전송하기 위한 유저 정보가 존재하지 않습니다."),
    NOTIFICATION_PROJECT_NOT_NULL("E060005", "알림을 전송하기 위한 프로젝트 정보가 존재하지 않습니다."),
    NOTIFICATION_ACHIEVEMENT_NOT_NULL("E060006", "알림을 전송하기 위한 업적 정보가 존재하지 않습니다."),
    NOTIFICATION_USER_RECEIVER_DUPLICATED("E060002", "리뷰 요청을 받는 유저와 알림을 전송하는 유저가 동일합니다."),

    INVALID_PROJECT_MEETING_TIME("E030101", "프로젝트 만남 시간이 유효하지 않습니다."),
    INVALID_PROJECT_TAG_COUNT("E030102", "프로젝트 태그 갯수가 유효하지 않습니다. 1개 이상 3개 이하로 입력해야 합니다."),
    INVALID_PROJECT_TAG_CONTENT_LENGTH("E030103",
        "프로젝트 태그 내용 길이가 유효하지 않습니다. 1자 이상 7자 이하로 입력해야 합니다."),
    INVALID_PROJECT_NULL_DATA("E030104", "프로젝트를 생성하기 위해 null 이여서는 안되는 데이터가 null 입니다."),
    NOT_MATCH_MEET_LOCATION("E030105", "프로젝트 만남 장소가 유저가 동네인증 한 구역이 아닙니다."),
    PROJECT_DELETION_NOT_ALLOWED("E030106", "매칭 중이거나 대기중인 프로젝트는 삭제할 수 없습니다."),
    PROJECT_CANCEL_NOT_ALLOWED("E030107", "진행 중인 프로젝트만 취소할 수 있습니다."),
    INVALID_MATCHING_USER_TO_CREATE_PROJECT("E030108", "매칭이 진행 중인 유저는 프로젝트 생성을 할 수 없습니다."),
    ALREADY_EXIST_PROGRESS_PROJECT("E030109", "종료되지 않은 프로젝트 카드가 있으면 프로젝트 생성을 할 수 없습니다."),
    INVALID_PROJECT_STATUS_TO_FINISH("E030110", "매칭이 진행 중인 프로젝트가 아니여서 프로젝트를 종료할 수 없습니다."),

    INVALID_SERVICE_REGION("E080101", "해당 지역은 서비스 지역이 아닙니다."),
    USER_DEVELOP_LANGUAGE_BAD_REQUEST("E020101", "개발 언어는 3개까지만 등록 가능합니다."),
    USER_WANTED_JOB_BAD_REQUEST("E020102", "희망 직무는 3개까지만 등록 가능합니다."),
    USERNAME_SHOULD_BE_NOT_EMPTY("E020103", "유저 이름은 비어있을 수 없습니다."),
    USER_REGION_SHOULD_BE_NOT_EMPTY("E020104", "유저 지역은 비어있을 수 없습니다."),
    USER_WANTED_JOB_DUPLICATE("E020105", "중복된 희망 직무가 있습니다."),
    USER_DEVELOP_LANGUAGE_DUPLICATE("E020106", "중복된 개발 언어가 있습니다."),
    USER_AVAILABLE_LIKE_COUNT_IS_ZERO("E020107", "취소할 수 있는 찔러보기 요청이 존재하지 않습니다."),
    USER_ID_NOT_NULL("E020001", "유저 아이디는 필수값입니다."),
    USER_AVATAR_URL_NOT_NULL("E020109", "유저 프로필 이미지는 필수값입니다."),
    USER_ALREADY_SIGNUP("E020110", "이미 회원가입한 유저입니다."),

    PROFILE_CARD_LIKE_ALREADY_EXIST("E040102", "이미 찔러보기 요청을 전송한 프로필 카드에 찔러보기 요청을 전송할 수 없습니다."),
    INVALID_PROFILE_CARD_LIKE_RECEIVER_INFO("E040103", "찔러보기 요청의 사용자가 존재하지 않습니다."),
    PROFILE_CARD_LIKE_AMOUNT_IS_ZERO("E040104", "프로필 카드의 찔러보기 요청이 존재하지 않습니다."),
    PROFILE_CARD_LIKE_NOT_EXIST("E040105", "해당 프로필 카드에 '찔러보기' 요청이 존재하지 않아 요청을 취소할 수 없습니다."),

    INVALID_PROJECT_STATUS_TO_ACCEPT("E050101", "프로젝트가 대기중이 아니여서 참여 요청을 수락할 수 없습니다."),
    INVALID_PROJECT_REQ_STATUS_TO_ACCEPT("E050102", "프로젝트 참여 요청이 대기중이 아니여서 참여 요청을 수락할 수 없습니다."),
    INVALID_MATCHING_USER_TO_ACCEPT("E050103", "매칭이 진행 중인 유저는 프로젝트 참여 요청을 수락할 수 없습니다."),
    INVALID_SENDER_TO_ACCEPT("E050104", "요청 보낸 상대가 이미 매칭중이기 때문에 프로젝트 참여 요청을 수락할 수 없습니다."),
    INVALID_PROJECT_REQ_STATUS_TO_CANCEL("E050105", "프로젝트 참여 요청이 대기중이 아니여서 참여 요청을 취소할 수 없습니다."),

    PROJECT_JOIN_REQUEST_ALREADY_EXIST("E090101", "이미 매칭 요청을 전송한 프로젝트에 매칭 요청을 생성할 수 없습니다."),
    INVALID_PROJECT_JOIN_REQUEST_REGION("E090102", "동네 인증한 구역에서만 프로젝트 매칭 요청을 생성할 수 있습니다."),
    PROJECT_JOIN_REQUEST_SHOULD_BE_ONLY_ONE("E090103", "프로젝트 매칭 요청은 한 번에 한 개만 전송할 수 있습니다."),
    INVALID_CREATOR_PROJECT_JOIN_REQUEST("E090104", "프로젝트 생성자는 해당 프로젝트에 매칭 요청을 전송할 수 없습니다."),
    MATCHING_CANCEL_NOT_ALLOWED("E090101", "이미 종료되거나 취소된 매칭은 취소할 수 없습니다."),

    CHAT_ROOM_CLOSED("E110101", "채팅방이 종료되어 채팅을 할 수 없습니다."),
    CHAT_ROOM_USER_CANNOT_DUPLICATE("E110102", "채팅방에 중복된 유저가 있습니다."),
    CHAT_ROOM_USER_NOT_CONTAINS("E110103", "채팅방에 해당 유저가 없습니다."),
    CHAT_ROOM_ALREADY_CLOSED("E110104", "채팅방이 이미 종료되었습니다."),
    CHAT_ROOM_SHOULD_NOT_NULL("E110105", "채팅방 정보가 존재하지 않습니다."),
    CHAT_USER_SHOULD_NOT_NULL("E110106", "채팅방 유저 정보가 존재하지 않습니다."),
    CHAT_ROOM_MAX_USER_SIZE("E110107", "채팅방에 최대 유저 수를 초과하여 채팅방을 생성할 수 없습니다."),

    REVIEW_SENDER_OR_RECEIVER_NOT_FOUND("E120101", "리뷰를 작성하기 위한 유저 정보가 존재하지 않습니다."),
    REVIEW_USER_DUPLICATED("E120102", "자신에 대한 리뷰는 작성할 수 없습니다."),
    REVIEW_PROJECT_NOT_NULL("E120103", "리뷰를 작성하기 위한 프로젝트 정보가 존재하지 않습니다."),
    REVIEW_ALREADY_EXISTS("E120104", "이미 작성된 리뷰가 존재합니다."),
    REVIEW_USER_NOT_MATCH("E120105", "리뷰 작성자와 리뷰 대상자가 일치하지 않습니다."),
    REVIEW_RATING_INVALID("E120106", "유효하지 않은 리뷰 평점입니다."),

    NON_ACHIEVED_USER_ACHIEVEMENT("E140101", "미달성 업적을 사용할 수 없습니다."),
    ACHIEVEMENT_SHOULD_BE_DIFFERENT("E140102", "이미 해당 업적을 대표 업적으로 사용중입니다."),
    ACHIEVEMENT_ALREADY_COMPLETED("E140103", "이미 달성한 업적입니다,"),

    INVALID_EVENT_LISTENER_REQUEST("E150101", "요청한 이벤트 처리에 실패하였습니다."),
    EVENT_LISTENER_REQUEST_FAILED("E150102", "요청한 이벤트가 예상치 못한 오류로 실패하였습니다."),

    EVENT_ALREADY_COMPLETED("E160101", "이미 해당 이벤트는 완료 처리되었습니다."),
    ;

    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    private final String code;
    private final String message;

    ErrorCode400(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
