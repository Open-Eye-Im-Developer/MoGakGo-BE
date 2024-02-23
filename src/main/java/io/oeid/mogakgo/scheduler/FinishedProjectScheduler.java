package io.oeid.mogakgo.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FinishedProjectScheduler {

    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;

    private final List<String> sqlFilePaths;

    public FinishedProjectScheduler(
        JdbcTemplate jdbcTemplate, ResourceLoader resourceLoader,
        @Value("${path.sql.finished-matched-project}") String finishedMatchedProjectSqlPath,
        @Value("${path.sql.finished-pending-project}") String finishedPendingProjectSqlPath,
        @Value("${path.sql.finished-project-request}") String finishedProjectRequestSqlPath
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceLoader = resourceLoader;
        this.sqlFilePaths = List.of(finishedMatchedProjectSqlPath, finishedPendingProjectSqlPath,
            finishedProjectRequestSqlPath);
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void executeSqlFile() throws IOException {
        for (String sqlFilePath : sqlFilePaths) {
            String sql = loadSqlFromFile(sqlFilePath);
            jdbcTemplate.execute(sql);
        }
    }

    private String loadSqlFromFile(String path) throws IOException {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(resourceLoader.getResource(path).getInputStream(),
                StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

}
