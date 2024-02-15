package io.oeid.mogakgo.domain.user.domain;

import io.oeid.mogakgo.domain.user.domain.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Entity
@Table(name = "user_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "github_id", nullable = false)
    private String githubId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "github_url", nullable = false)
    private String githubUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private User(Long id, String githubId, String avatarUrl, String githubUrl) {
        this.id = id;
        this.username = githubId;
        this.githubId = githubId;
        this.avatarUrl = avatarUrl;
        this.githubUrl = githubUrl;
        this.role = Role.ROLE_USER;
    }

    public static User of(long id, String username, String avatarUrl, String githubUrl) {
        return new User(id, username, avatarUrl, githubUrl);
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

}