package io.oeid.mogakgo.core.configuration;

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "outboxTaskExecutor")
    public ThreadPoolTaskExecutor outboxTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);

        // corePoolSize의 스레드에서 처리하다가 처리 속도가 밀릴 경우 작업을 큐에 대기
        // 큐 사이즈 이상의 요청이 들어오면 maxPoolSize의 스레드를 생성해서 작업을 처리
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());
        return taskExecutor;
    }

    @Bean(name = "achievementTaskExecutor")
    public ThreadPoolTaskExecutor achievementTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("Achievement-Executor-");
        taskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());
        return taskExecutor;
    }

    @Bean(name = "notificationTaskExecutor")
    public ThreadPoolTaskExecutor notificationTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setQueueCapacity(50);
        taskExecutor.setThreadNamePrefix("Notification-Executor-");
        taskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());
        return taskExecutor;
    }

}
