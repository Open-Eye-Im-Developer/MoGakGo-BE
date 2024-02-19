package io.oeid.mogakgo.domain.achievement.domain;

import io.oeid.mogakgo.domain.achievement.domain.enums.AchievementType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "achievement_tb")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "img_url")
    private String imgUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AchievementType achievementType;

    @Builder
    private Achievement(String title, String description, String imgUrl,
        AchievementType achievementType) {
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.achievementType = achievementType;
    }
}
