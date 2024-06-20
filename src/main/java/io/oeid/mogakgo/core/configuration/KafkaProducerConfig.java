package io.oeid.mogakgo.core.configuration;

import io.oeid.mogakgo.domain.event.Event;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/*
 * Deprecated annotation programming -> change to functional programming
 * @EnableBinding
 * @Input
 * @Output
 * @StreamListener
 * @StreamMessageConverter
 * ...
 */

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    @Value("${spring.kafka.producer.transaction-id-prefix}")
    private String TRANSACTION_ID_PREFIX;

    @Value("${spring.kafka.producer.properties.idempotence}")
    private Boolean IDEMPOTENCE;

    @Value("${spring.kafka.producer.properties.max-in-flight-requests-per-connection}")
    private Integer MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION;

    @Value("${spring.kafka.producer.retries}")
    private Integer RETRIES;

    @Value("${spring.kafka.producer.acks}")
    private String ACKS;


    /**
     * BOOTSTRAP_SERVERS_CONFIG
     * producer가 처음으로 연결할 kafka broker의 위치 설정
     * ---
     * KEY_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CLASS_CONFIG
     * kafka는 네트워크를 통해 데이터를 전송하기 떄문에 객체를 byte array로 변환하는 직렬화 과정이 필요함.
     * producer가 key와 value 값의 데이터를 kafka broker로 전송하기 전에 데이터를 byte array로 변환하는데
     * 사용하는 직렬화 메커니즘을 설정.
     */

    @Bean
    public ProducerFactory<String, Event<?>> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, ACKS);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, IDEMPOTENCE);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, TRANSACTION_ID_PREFIX);
        // TODO: 트랜잭션 최적화와 프로듀서의 높은 메시지 전송률을 고려하여 설정해야 함. 트랜잭션 사용 시, 1로 설정할 것을 권장
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION);
        props.put(ProducerConfig.RETRIES_CONFIG, RETRIES);

        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Spring Kafka에서 제공하는 Kafka producer를 Wrapping한 클래스
     * Kafka에 메시지 발생을 위한 여러 메서드를 제공함.
     */

    @Bean
    public KafkaTemplate<String, Event<?>> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }



}
