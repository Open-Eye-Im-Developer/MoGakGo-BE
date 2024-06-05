package io.oeid.mogakgo.core.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oeid.mogakgo.core.properties.kafka.MessageConsumer;
import io.oeid.mogakgo.core.properties.kafka.MessageProducer;
import io.oeid.mogakgo.domain.event.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 3,
               brokerProperties = {
                  "listeners=PLAINTEXT://localhost:9092"
               },
               ports = { 9092 })
public class SimpleKafkaTest {

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageConsumer messageConsumer;

    @Test
    void name() throws Exception {
        // given
        Event event = Event.idOf(1L);
        String payload = objectMapper.writeValueAsString(event);

        // when
        messageProducer.sendMessage("my-topic", payload);
        Thread.sleep(2000);

        // then
        org.junit.jupiter.api.Assertions.assertNotEquals(0, messageConsumer.getEventRepo().size());
    }
}
