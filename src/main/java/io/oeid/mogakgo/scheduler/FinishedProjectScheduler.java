package io.oeid.mogakgo.scheduler;

import io.oeid.mogakgo.domain.notification.application.FCMNotificationService;
import io.oeid.mogakgo.domain.notification.application.NotificationService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FinishedProjectScheduler {

    private final JdbcTemplate jdbcTemplate;
    private final Resource[] sqlStatements;
    private final NotificationService notificationService;
    private final FCMNotificationService fcmNotificationService;

    protected FinishedProjectScheduler(JdbcTemplate jdbcTemplate,
        @Qualifier("webApplicationContext") ResourcePatternResolver resourcePatternResolver,
        @Value("${path.schedule.sql}") String sqlStatementsPath,
        NotificationService notificationService, FCMNotificationService fcmNotificationService
    ) throws IOException {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlStatements = resourcePatternResolver.getResources(sqlStatementsPath);
        this.notificationService = notificationService;
        this.fcmNotificationService = fcmNotificationService;
    }

    @Scheduled(cron = "* * * * * ?") // 매일 자정에 실행
    public void executeSqlFile() {
        for (Resource statement : sqlStatements) {
            String sql = loadSqlFromFile(statement);
            jdbcTemplate.execute(sql);
        }
        sendReviewNotification();
        sendMatchFailNotification();
    }

    private String loadSqlFromFile(Resource resource) {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error("sql 스케쥴링 실행 중 오류 발생 : " + resource.getFilename(), e);
            return "";
        }
    }

    private void sendReviewNotification() {
        jdbcTemplate.query(
            "SELECT pt.id, mt.sender_id, pt.creator_id FROM matching_tb mt LEFT JOIN project_tb pt on mt.project_id = pt.id WHERE mt.matching_status = 'FINISHED' and DATE(pt.meet_start_time) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)",
            rch -> {
                Long senderId = rch.getLong("sender_id");
                Long projectId = rch.getLong("id");
                Long creatorId = rch.getLong("creator_id");
                notificationService.createReviewRequestNotification(senderId, creatorId, projectId);
                //fcmNotificationService.sendNotification(senderId, creatorId, projectId);
                notificationService.createReviewRequestNotification(creatorId, senderId, projectId);
                //fcmNotificationService.sendNotification(creatorId, senderId, projectId);
            }
        );
    }

    private void sendMatchFailNotification() {
        jdbcTemplate.query(
            "SELECT pjrt.sender_id, pjrt.project_id FROM mogakgo.project_join_request_tb pjrt WHERE pjrt.join_request_status = 'REJECTED' and DATE(pjrt.created_at) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)",
            rch -> {
                Long projectId = rch.getLong("project_id");
                Long senderId = rch.getLong("sender_id");
                notificationService.createMatchingFailedNotification(senderId, projectId);
            }
        );
    }

}
