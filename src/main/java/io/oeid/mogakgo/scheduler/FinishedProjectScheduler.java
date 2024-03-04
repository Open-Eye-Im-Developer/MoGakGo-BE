package io.oeid.mogakgo.scheduler;

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

    private FinishedProjectScheduler(
        JdbcTemplate jdbcTemplate,
        @Qualifier("webApplicationContext") ResourcePatternResolver resourcePatternResolver,
        @Value("${path.schedule.sql}") String sqlStatementsPath
    ) throws IOException {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlStatements = resourcePatternResolver.getResources(sqlStatementsPath);
    }

    @Scheduled(cron = "* * * * * ?") // 매일 자정에 실행
    public void executeSqlFile() {
        for (Resource statement : sqlStatements) {
            String sql = loadSqlFromFile(statement);
            jdbcTemplate.execute(sql);
        }
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

}
