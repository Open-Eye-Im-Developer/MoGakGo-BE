package io.oeid.mogakgo.domain.project.presentation.dto.res;

import io.oeid.mogakgo.domain.project.domain.entity.ProjectTag;
import io.oeid.mogakgo.domain.user.domain.User;
import io.oeid.mogakgo.domain.user.domain.UserDevelopLanguageTag;
import io.oeid.mogakgo.domain.user.domain.UserWantedJobTag;
import io.oeid.mogakgo.domain.user.domain.enums.DevelopLanguage;
import io.oeid.mogakgo.domain.user.domain.enums.WantedJob;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Schema(description = "프로젝트 카드 조회 리스트 응답 DTO")
@Getter
public class ProjectDetailAPIRes {

    private final Long projectId;
    private final String username;
    private final String githubId;
    private final String avatarUrl;
    private final String githubUrl;
    private final String bio;
    private final double jandiRate;
    private final List<DevelopLanguage> developLanguages;
    private final List<WantedJob> wantedJobs;
    private final List<ProjectTag> projectTags;
    private final LocalDateTime meetStartTime;
    private final LocalDateTime meetEndTime;
    private final String meetDetail;

    private ProjectDetailAPIRes(Long projectId, String username, String githubId, String avatarUrl,
        String githubUrl, String bio, double jandiRate, List<DevelopLanguage> developLanguages,
        List<WantedJob> wantedJobs, List<ProjectTag> projectTags, LocalDateTime meetStartTime,
        LocalDateTime meetEndTime, String meetDetail) {
        this.projectId = projectId;
        this.username = username;
        this.githubId = githubId;
        this.avatarUrl = avatarUrl;
        this.githubUrl = githubUrl;
        this.bio = bio;
        this.jandiRate = jandiRate;
        this.developLanguages = developLanguages;
        this.wantedJobs = wantedJobs;
        this.projectTags = projectTags;
        this.meetStartTime = meetStartTime;
        this.meetEndTime = meetEndTime;
        this.meetDetail = meetDetail;
    }

    public static ProjectDetailAPIRes of(Long projectId, User user, List<ProjectTag> projectTags,
        LocalDateTime meetStartTime, LocalDateTime meetEndTime, String meetDetail) {
        return new ProjectDetailAPIRes(
            projectId,
            user.getUsername(),
            user.getGithubId(),
            user.getAvatarUrl(),
            user.getGithubUrl(),
            user.getBio(),
            user.getJandiRate(),
            user.getUserDevelopLanguageTags().stream().map(
                UserDevelopLanguageTag::getDevelopLanguage).toList(),
            user.getUserWantedJobTags().stream().map(
                UserWantedJobTag::getWantedJob).toList(),
            projectTags,
            meetStartTime,
            meetEndTime,
            meetDetail
        );
    }
}
