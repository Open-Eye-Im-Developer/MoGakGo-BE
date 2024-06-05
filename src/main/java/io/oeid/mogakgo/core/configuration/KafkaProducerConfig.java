package io.oeid.mogakgo.core.configuration;

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

/**
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
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Spring Kafka에서 제공하는 Kafka producer를 Wrapping한 클래스
     * Kafka에 메시지 발생을 위한 여러 메서드를 제공함.
     */

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
