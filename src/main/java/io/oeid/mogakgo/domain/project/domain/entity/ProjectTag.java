package io.oeid.mogakgo.domain.project.domain.entity;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_PROJECT_TAG_CONTENT_LENGTH;

import io.oeid.mogakgo.domain.project.exception.ProjectException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "project_tag_TB")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    private ProjectTag(Long id, String content, Project project) {
        this.id = id;
        this.project = project;
        setContentWithValidate(content);
    }

    public static ProjectTag of(String content, Project project) {
        return ProjectTag.builder()
            .content(content)
            .project(project)
            .build();
    }

    private void setContentWithValidate(String content) {
        if (content != null) {
            content = content.replaceAll("\\s+", "");
            if (!content.isEmpty() && content.length() < 8) {
                this.content = content;
                return;
            }
        }
        throw new ProjectException(INVALID_PROJECT_TAG_CONTENT_LENGTH);
    }
}
