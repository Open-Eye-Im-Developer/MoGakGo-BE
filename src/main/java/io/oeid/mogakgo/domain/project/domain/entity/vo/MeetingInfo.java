package io.oeid.mogakgo.domain.project.domain.entity.vo;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_MEETING_TIME;
import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_NULL_DATA;

import io.oeid.mogakgo.domain.project.exception.ProjectException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingInfo {

    @Column(name = "meet_start_time")
    private LocalDateTime meetStartTime;

    @Column(name = "meet_end_time")
    private LocalDateTime meetEndTime;

    @Column(name = "meet_location")
    private Point meetLocation;

    @Column(name = "meet_detail")
    private String meetDetail;

    private MeetingInfo(
        LocalDateTime meetStartTime, LocalDateTime meetEndTime, Point meetLocation,
        String meetDetail
    ) {
        this.meetStartTime = meetStartTime;
        this.meetEndTime = meetEndTime;
        this.meetLocation = meetLocation;
        this.meetDetail = meetDetail;
        validateMeetingTime();
    }

    public static MeetingInfo of(
        LocalDateTime meetStartTime, LocalDateTime meetEndTime, Point meetLocation,
        String meetDetail
    ) {
        return new MeetingInfo(meetStartTime, meetEndTime, meetLocation, meetDetail);
    }

    private void validateMeetingTime() {
        if (meetStartTime == null || meetEndTime == null) {
            throw new ProjectException(INVALID_PROJECT_NULL_DATA);
        }
        // 현재 서버 시간 기준으로 당일 날짜 확인
        LocalDateTime now = LocalDateTime.now();
        boolean isToday = meetStartTime.toLocalDate().equals(now.toLocalDate()) &&
            meetEndTime.toLocalDate().equals(now.toLocalDate());
        // meetEndTime이 meetStartTime보다 뒤에 있는지 확인
        boolean isTimeOrderValid = meetEndTime.isAfter(meetStartTime);
        // 시간 차이가 30분 단위인지 확인
        boolean isTimeDiffValid = ChronoUnit.MINUTES.between(meetStartTime, meetEndTime) % 30 == 0;

        if (!isToday || !isTimeOrderValid || !isTimeDiffValid) {
            throw new ProjectException(INVALID_PROJECT_MEETING_TIME);
        }
    }

}
