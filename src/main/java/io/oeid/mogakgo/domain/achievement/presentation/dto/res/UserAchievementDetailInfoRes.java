package io.oeid.mogakgo.domain.achievement.presentation.dto.res;

import io.oeid.mogakgo.domain.achievement.domain.entity.enums.RequirementType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "사용자에 대한 업적 상세 정보 조회 응답 DTO")
@Getter
public class UserAchievementDetailInfoRes {

    @Schema(description = "조회할 업적 ID")
    private final Long achievementId;

    @Schema(description = "업적을 조회할 사용자 ID", example = "11", implementation = Long.class)
    private final Long userId;

    @Schema(description = "조회할 업적 타이틀")
    private final String title;

    @Schema(description = "조회할 업적 이미지 Url")
    private final String imgUrl;

    @Schema(description = "조회할 업적 상세 설명")
    private final String description;

    @Schema(description = "조회할 업적 단계")
    private final Integer progressLevel;

    @Schema(description = "조회할 업적 타입")
    private final RequirementType requirementType;

    @Schema(description = "업적 달성을 위해 만족해야 하는 값")
    private final Integer requirementValue;

    @Schema(description = "현재 업적 달성을 위해 진행된 횟수")
    private final Integer progressCount;

    @Schema(description = "해당 업적의 달성 여부")
    private final Boolean completed;

    @Builder
    private UserAchievementDetailInfoRes(Long achievementId, Long userId, String title,
        String imgUrl, String description, Integer progressLevel, RequirementType requirementType,
        Integer requirementValue, Integer progressCount, Boolean completed) {
        this.achievementId = achievementId;
        this.userId = userId;
        this.title = title;
        this.imgUrl = imgUrl;
        this.description = description;
        this.progressLevel = progressLevel;
        this.requirementType = requirementType;
        this.requirementValue = requirementValue;
        this.progressCount = progressCount;
        this.completed = completed;
    }
}