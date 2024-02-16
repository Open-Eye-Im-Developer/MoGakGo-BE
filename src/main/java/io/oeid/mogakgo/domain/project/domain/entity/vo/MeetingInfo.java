package io.oeid.mogakgo.domain.project.domain.entity.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

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
            throw new RuntimeException("startTime and endTime are required!");
        }
        LocalDate today = LocalDate.now();
        if (!meetStartTime.toLocalDate().equals(today) || !meetEndTime.toLocalDate()
            .equals(today)) {
            throw new RuntimeException("meeting is only available on the same day!");
        }
        if (meetEndTime.isBefore(meetStartTime) || meetEndTime.isEqual(meetStartTime)) {
            throw new RuntimeException("endTime must be later than the startTime!");
        }
    }

}
