package io.oeid.mogakgo.core.kafka.producer;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oeid.mogakgo.common.generator.UUIDGenerator;
import io.oeid.mogakgo.core.properties.event.vo.AchievementEvent;
import io.oeid.mogakgo.core.properties.kafka.AchievementMessageConsumer;
import io.oeid.mogakgo.core.properties.kafka.MessageProducer;
import io.oeid.mogakgo.domain.achievement.domain.entity.enums.ActivityType;
import io.oeid.mogakgo.domain.event.Event;
import io.oeid.mogakgo.domain.log.domain.entity.MessageLog;
import io.oeid.mogakgo.domain.log.infrastructure.MessageLogJpaRepository;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@EmbeddedKafka(partitions = 3,
               brokerProperties = {
                  "listeners=PLAINTEXT://localhost:59092",
                   "offsets.topic.replication.factor=1",
                   "transaction.state.log.replication.factor=1",
                   "transaction.state.log.min.isr=1"
               },
               ports = { 59092 })
public class SimpleKafkaTest {

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AchievementMessageConsumer messageConsumer;

    @Autowired
    private MessageLogJpaRepository messageLogRepository;

//    @Test
//    void name() throws Exception {
//        // given
//        //Event event = Event.idOf(1L);
//        //String payload = objectMapper.writeValueAsString(event);
//
//        // when
//        // messageProducer.sendMessage("my-topic", payload);
//        Thread.sleep(6000);
//
//        // then
//        //assertNotEquals(0, messageConsumer.getEventRepo().size());
//        //org.junit.jupiter.api.Assertions.assertEquals(2, messageConsumer.getEventRepo().size());
//    }
//
//    @Test
//    void 여러_개의_메시지_소비하기() throws Exception {
//
//        Long id = 1L;
//
//        while (id < 5L) {
//            //Event event = Event.idOf(id);
//            //String payload = objectMapper.writeValueAsString(event);
//
//            // messageProducer.sendMessage("my-topic", payload);
//            id += 1L;
//        }
//
//        Thread.sleep(5000);
//        //messageConsumer.printEvent();
//    }
//
//    @Test
//    void 여러_파티션의_메시지_소비하기() throws Exception {
//
//        Long id = 1L;
//        Integer num = 1;
//
//        while (id < 5L) {
//            //Event event = Event.idOf(id);
//            //String payload = objectMapper.writeValueAsString(event);
//
//            // messageProducer.sendMessage("my-topic", payload);
//            id += 1L;
//            num++;
//            if (num > 2) num = 0;
//        }
//    }
//
//    @Test
//    void 카프카_메시지_타입() throws Exception {
//
//        AchievementEvent event = AchievementEvent.builder()
//            .userId(11L)
//            .activityType(ActivityType.FRESH_DEVELOPER)
//            .build();
//
//        messageProducer.sendMessage("achievement", Event.<AchievementEvent>builder()
//            .event(event)
//            .build()
//        );
//
//        Thread.sleep(5000);
//        //assertNotEquals(0, messageConsumer.getRepo().size());
//    }
//
//    @Test
//    void 카프카_메시지_전송_트랜잭션_테스트() throws Exception {
//
//        AchievementEvent event = AchievementEvent.builder()
//            .userId(11L)
//            .activityType(ActivityType.FRESH_DEVELOPER)
//            .build();
//
//        messageProducer.sendMessage("achievement", Event.<AchievementEvent>builder()
//            .event(event)
//            .build()
//        );
//
//        Thread.sleep(5000);
//        //assertNotEquals(0, messageConsumer.getRepo().size());
//    }
//
//    @Test
//    void CompletableFuture_롤백_테스트() throws Exception {
//
//        AchievementEvent event = AchievementEvent.builder()
//            .userId(11L)
//            .activityType(ActivityType.FRESH_DEVELOPER)
//            .build();
//
////        messageProducer.send("achievement", Event.<AchievementEvent>builder()
////            .event(event)
////            .build())
////            .whenComplete((res, ex) -> {
////                if (ex != null) {
////                    System.out.println("fail!");
////                } else if (res != null) {
////                    // 여기에 걸린 순간, 발행된 메시지는 이미 커밋됨
////                    process(true);
////                }
////            });
//
//        assertNotEquals(messageLogRepository.findAll().size(), 1);
//
//    }
//
//    public void process(boolean fail) {
//
//        MessageLog messageLog = MessageLog.builder()
//            .eventId(UUIDGenerator.generateUUID())
//            .build();
//        messageLogRepository.save(messageLog);
//
//        if (fail) {
//            throw new RuntimeException("Simulated failure");
//        }
//
//        MessageLog messageLog2 = MessageLog.builder()
//            .eventId(UUIDGenerator.generateUUID())
//            .build();
//        messageLogRepository.save(messageLog2);
//    }
//
//    @Test
//    void 메시지_소비_실패로_인한_DLT_이동_메일_전송_테스트() {
//
//        AchievementEvent event = AchievementEvent.builder()
//            .userId(11L)
//            .activityType(ActivityType.FRESH_DEVELOPER)
//            .build();
//
//        messageProducer.sendMessage("achievement", Event.<AchievementEvent>builder()
//            .event(event)
//            .build()
//        );
//    }
}
