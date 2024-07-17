package io.oeid.mogakgo.common.handler.mail;

import io.oeid.mogakgo.domain.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailHandler {

    private static final String SUBJECT = "DLT Alert: Message Consumption Failed!";

    @Value("${spring.mail.username}")
    private String admin;

    private final JavaMailSender mailSender;

    public void postProcessDltMessage(
        ConsumerRecord<String, Event<?>> record,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partitionId,
        @Header(KafkaHeaders.OFFSET) Long offset,
        @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errMessage
    ) {

        String eventId = record.value().getId();

        log.error("""
            [DLT Alert] message with eventId '{}' was moved to '{}.DLT', and sent email to Admin Successfully!
            """, eventId, topic);

        // message Processing...
        sendEmailToAdmin(eventId, topic, partitionId, offset, errMessage);
    }

    private void sendEmailToAdmin(String id, String topic, int partitionId, Long offset, String errMessage) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(admin);
        mailMessage.setSubject(SUBJECT);
        mailMessage.setText(String.format("""
            A message has been moved to the %s.DLT!
            MessageId: %s
            Topic: %s
            Partition: %d
            Offset: %d
            Exception: %s
            """, topic, id, topic, partitionId, offset, errMessage));
        mailSender.send(mailMessage);
    }
}
