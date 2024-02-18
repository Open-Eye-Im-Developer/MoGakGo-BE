package io.oeid.mogakgo.domain.user.domain;

import io.oeid.mogakgo.domain.user.domain.enums.DevelopLanguage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "user_develop_language_tb")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDevelopLanguageTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "develop_language")
    private DevelopLanguage developLanguage;

    @Setter
    @Column(name = "byte_size", nullable = false)
    private Integer byteSize;

    @Builder
    private UserDevelopLanguageTag(User user, DevelopLanguage developLanguage, Integer byteSize) {
        this.user = user;
        this.developLanguage = developLanguage;
        this.byteSize = byteSize;
        user.addDevelopLanguage(this);
    }

}
