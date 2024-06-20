package io.oeid.mogakgo.core.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor executor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setMaxPoolSize(30);

        // corePoolSize의 스레드에서 처리하다가 처리 속도가 밀릴 경우 작업을 큐에 대기
        // 큐 사이즈 이상의 요청이 들어오면 maxPoolSize의 스레드를 생성해서 작업을 처리
        taskExecutor.setQueueCapacity(100);
        taskExecutor.setThreadNamePrefix("Executor-");
        taskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());
        return taskExecutor;
    }

}
