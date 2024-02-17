package io.oeid.mogakgo.domain.project.presentation.dto.req;

import io.oeid.mogakgo.domain.project.domain.entity.Project;
import io.oeid.mogakgo.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "프로젝트 카드 생성 요청")
@Getter
@NoArgsConstructor
public class ProjectCreateReq {

    @Schema(description = "프로젝트를 생성하는 유저 ID", example = "2", implementation = Long.class)
    @NotNull
    private Long creatorId;

    @Schema(description = "미팅 시작 시간. 30분 단위만 가능.", example = "2024-02-18T12:00:00",
        pattern = "yyyy-MM-dd'T'HH:mm:ss", type = "string")
    @NotNull
    @PastOrPresent
    private LocalDateTime meetStartTime;

    @Schema(description = "미팅 종료 시간. 30분 단위만 가능.", example = "2024-02-18T12:30:00",
        pattern = "yyyy-MM-dd'T'HH:mm:ss", type = "string")
    @NotNull
    @PastOrPresent
    private LocalDateTime meetEndTime;

    @Schema(description = "미팅 장소의 위도", example = "37.63338336616322", implementation = Double.class)
    @NotNull
    @DecimalMin("32.0")
    @DecimalMax("39.0")
    private Double meetLat;

    @Schema(description = "미팅 장소의 경도", example = "127.0783098757533", implementation = Double.class)
    @NotNull
    @DecimalMin("123.0")
    @DecimalMax("132.0")
    private Double meetLng;

    @Schema(description = "미팅 장소의 상세 설명", example = "서울과학기술대학교", implementation = String.class)
    @NotBlank
    private String meetDetail;

    @Schema(description = "프로젝트 태그 목록", implementation = ProjectTagCreateReq.class,
        example = "[{\"content\":\"인싸\"},{\"content\":\"말많은\"}]")
    @NotEmpty
    private List<@Valid ProjectTagCreateReq> tags;


    public Project toEntity(User creator) {
        return Project.builder()
            .creator(creator)
            .meetStartTime(meetStartTime)
            .meetEndTime(meetEndTime)
            .meetLat(meetLat)
            .meetLng(meetLng)
            .meetDetail(meetDetail)
            .projectTags(tags)
            .build();
    }

}
