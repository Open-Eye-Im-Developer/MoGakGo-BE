package io.oeid.mogakgo.core.configuration;

import io.oeid.mogakgo.common.handler.kafka.DltProcessor;
import io.oeid.mogakgo.common.handler.mail.MailHandler;
import io.oeid.mogakgo.core.properties.kafka.constant.KafkaConsumerConstants;
import io.oeid.mogakgo.core.properties.kafka.deserializer.CustomDeserializer;
import io.oeid.mogakgo.domain.event.Event;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.EndpointHandlerMethod;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_ADDRESS;

    @Value("${spring.kafka.consumer.group-id}")
    private String GROUP_ID;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String AUTO_OFFSET_RESET;

    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private boolean AUTO_COMMIT;

    @Value("${spring.kafka.consumer.max-poll-records}")
    private Integer MAX_POLL_RECORDS;

    @Value("${spring.kafka.consumer.isolation-level}")
    private String ISOLATION_LEVEL;

    private final MailHandler mailHandler;

    @Bean
    public ConsumerFactory<String, Event<?>> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_ADDRESS);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, AUTO_COMMIT);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, CustomDeserializer.class.getName());
        // 정확한 한 번, Exactly-Once를 위한 옵션
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, ISOLATION_LEVEL);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, MAX_POLL_RECORDS);

        return new DefaultKafkaConsumerFactory<>(props,
            new StringDeserializer(),
            new ErrorHandlingDeserializer<>(new CustomDeserializer()));
    }

    @Bean
    public DefaultErrorHandler dlqErrorHandler(KafkaTemplate<String, Event<?>> kafkaTemplate) {
        DeadLetterPublishingRecoverer dlqRecover = new DeadLetterPublishingRecoverer(kafkaTemplate,
            (record, e) -> {
                log.error("""
                            [DLT log] received message '{}' was thrown, cause '{}' 
                            with topic '{}', partition '{}', offset '{}'
                        """,
                    record.value(), e.getMessage(), record.topic(), record.partition(), record.offset());
                // TODO: 재시도 실패로 인해 DLT로 메시지가 전송되면 메일 알림이 자동으로 전송되도록 구현
                mailHandler.postProcessDltMessage((ConsumerRecord<String, Event<?>>) record,
                    record.topic(), record.partition(), record.offset(), e.getMessage());
                return new TopicPartition(record.topic() + ".DLT", record.partition());
            });

        var fixedBackOff = new FixedBackOff(1000L, 2L);
        var errorHandler = new DefaultErrorHandler(dlqRecover, fixedBackOff);
        errorHandler.addRetryableExceptions(SocketTimeoutException.class, RuntimeException.class);
        errorHandler.addNotRetryableExceptions(NullPointerException.class);
        return errorHandler;
    }

    /*
     * KafkaListenerContainerFactory
     * @KafkaListener 어노테이션이 붙은 메서드에 주입되어 사용됨
     * 메시지를 동시에 처리할 수 있는 messageListenerContainer를 생성함
     * Spring Kafka에서 Kafka 메시지를 수신하고 처리하기 위한 Listener Container를 생성하는 팩토리
     * Kafka 브로커로부터 메시지를 가져와 애플리케이션에서 정의한 메서드로 전달
     * 1. Listener Container 설정
     *  - 브로커와의 연결 설정
     *  - 메시지 소비 관련 연결 설정 (예: 컨슈머 그룹, 오프셋 관리, 에러 처리 ···)
     * 2. Error Handling
     *  - 메시지 처리 중 발생하는 예외 처리
     *  - 기본적으로 'SeekToCurrentErrorHandler' 혹은 CustomErrorHandler 사용
     * 3. Parallelism
     * 4. Manage Thread
     *  - 동시에 실행되는 Listener 개수와 Thread Pool 설정
     */

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Event<?>> kafkaListenerContainerFactory(
        KafkaTemplate<String, Event<?>> kafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Event<?>> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setBatchListener(true);
        // TODO: AckMode.RECORD 모드와 비교분석 필요
        factory.getContainerProperties().setAckMode(AckMode.MANUAL);
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(dlqErrorHandler(kafkaTemplate));
        return factory;
    }

    /*
     * RetryTopicConfiguration을 Bean으로 등록하면, 전역적으로 재시도 · DLT 토픽이 적용됨
     * 메시지 처리 실패 시 1차적으로 origin 토픽에서 특정 간격 후에 메시지 재소비
     * 설정된 최대 재시도 횟수에 도달하면 DLT 토픽으로 전송되며, 관리자가 별도로 처리할 수 있음
     * listenerFactory 설정은 @KafkaListener의 factory 설정과 동일해야 함
     * -- MessageConversionException 방지 위함
     * 1. 재시도 메커니즘 설정
     * 2. 재시도 토픽과 DLT 생성 및 설정
     *
     * 3. 재시도 및 에러 헨들링 로직 구성
     *  - 메시지 소비 실패 시에, 재시도 로직과 DLT로 보내는 로직을 자동으로 구성
     */

    // TODO: DefaultErrorHandler와의 장단점 비교 필요, 추후 변경 가능
    public RetryTopicConfiguration retryTopicConfig(KafkaTemplate<String, Event<?>> kafkaTemplate) {

        return RetryTopicConfigurationBuilder
            .newInstance()
            // 재시도 및 DLT 자동 생성할 때 사용할 설정 지정 -> 주로 새 토픽의 타피션 수와 복제 인수를 설정함
            // first param: partitions / second param: replicas
            .autoCreateTopicsWith(KafkaConsumerConstants.PARTITION_COUNT, KafkaConsumerConstants.REPLICA_COUNT)
            .maxAttempts(KafkaConsumerConstants.MAX_ATTEMPT_COUNT)
            .fixedBackOff(KafkaConsumerConstants.BACK_OFF_PERIOD)
            .retryTopicSuffix(".RETRY")
            .dltSuffix(".DLT")
            .listenerFactory(kafkaListenerContainerFactory(kafkaTemplate))
            .setTopicSuffixingStrategy(TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
            // DLT로 전송된 메세지를 처리할 메서드 설정
            .dltHandlerMethod(new EndpointHandlerMethod(DltProcessor.class, "postProcessDltMessage"))
            .create(kafkaTemplate);
    }

}
