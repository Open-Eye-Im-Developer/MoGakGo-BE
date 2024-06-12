package io.oeid.mogakgo.core.configuration;

import io.oeid.mogakgo.common.handler.kafka.DltProcessor;
import io.oeid.mogakgo.core.properties.kafka.constant.KafkaConsumerConstants;
import io.oeid.mogakgo.core.properties.kafka.deserializer.CustomDeserializer;
import io.oeid.mogakgo.domain.event.Event;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
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

        return new DefaultKafkaConsumerFactory<>(props,
            new StringDeserializer(),
            new ErrorHandlingDeserializer<>(new CustomDeserializer()));
    }

    /*
     * KafkaListenerContainerFactory
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

        var recover = new DeadLetterPublishingRecoverer(kafkaTemplate);
        var fixedBackOff = new FixedBackOff(1000L, 2L);
        var defaultErrorHandler = new DefaultErrorHandler(recover, fixedBackOff);

        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(defaultErrorHandler);
        return factory;
    }

    /*
     * RetryTopicConfiguration을 Bean으로 등록하면, 전역적으로 재시도 · DLT 토픽이 적용됨
     * listenerFactory 설정은 @KafkaListener의 factory 설정과 동일해야 함
     * -- MessageConversionException 방지 위함
     * 1. 재시도 메커니즘 설정
     * 2. 재시도 토픽과 DLT 생성 및 설정
     * 3. 재시도 및 에러 헨들링 로직 구성
     *  - 메시지 소비 실패 시에, 재시도 로직과 DLT로 보내는 로직을 자동으로 구성
     */

    @Bean
    public RetryTopicConfiguration retryTopicConfig(KafkaTemplate<String, Event<?>> kafkaTemplate)
        throws NoSuchMethodException {

        return RetryTopicConfigurationBuilder
            .newInstance()
            .autoCreateTopicsWith(KafkaConsumerConstants.REPLICA_COUNT, KafkaConsumerConstants.REPLICATION_FACTOR)
            .maxAttempts(KafkaConsumerConstants.MAX_ATTEMPT_COUNT)
            .fixedBackOff(KafkaConsumerConstants.BACK_OFF_PERIOD)
            .retryTopicSuffix(".RETRY")
            .dltSuffix(".DLT")
            .listenerFactory(kafkaListenerContainerFactory(kafkaTemplate))
            .setTopicSuffixingStrategy(TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
            .dltHandlerMethod(new EndpointHandlerMethod(DltProcessor.class, "postProcessDltMessage"))
            .create(kafkaTemplate);
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        var fixedBackOff = new FixedBackOff(1000L, 2L);
        var errorHandler = new DefaultErrorHandler((consumerRecord, e) -> {
            // ...
        }, fixedBackOff);
        errorHandler.addRetryableExceptions();
        errorHandler.addNotRetryableExceptions();
        return errorHandler;
    }

    /*
     * @KafkaListener 어노테이션이 붙은 메서드에 주입되어 사용됨
     * 메시지를 동시에 처리할 수 있는 messageListenerContainer를 생성함

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    */
}
