package io.oeid.mogakgo.core.properties.kafka.constant;

public interface KafkaConsumerConstants {

    int MAX_ATTEMPT_COUNT = 10;
    Long BACK_OFF_PERIOD = 1000L;
    int PARTITION_COUNT = 3;
    short REPLICA_COUNT = 1;

}
