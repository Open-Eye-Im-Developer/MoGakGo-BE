package io.oeid.mogakgo.domain.achievement.presentation;

import io.oeid.mogakgo.common.annotation.UserId;
import io.oeid.mogakgo.common.swagger.template.AchievementSwagger;
import io.oeid.mogakgo.domain.achievement.application.AchievementService;
import io.oeid.mogakgo.domain.achievement.application.dto.res.UserAchievementInfoRes;
import io.oeid.mogakgo.domain.achievement.presentation.dto.res.UserAchievementDetailAPIRes;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/achievements")
@RequiredArgsConstructor
public class AchievementController implements AchievementSwagger {

    private final AchievementService achievementService;

    @GetMapping("/{id}")
    public ResponseEntity<List<UserAchievementDetailAPIRes>> getUserAchievementDetail(
        @UserId Long userId, @PathVariable Long id
    ) {
        List<UserAchievementInfoRes> userAchievementLists = achievementService
            .getUserAchievementInfo(userId, id);

        return ResponseEntity.ok().body(
            userAchievementLists.stream().map(UserAchievementDetailAPIRes::from).toList()
        );
    }

}
