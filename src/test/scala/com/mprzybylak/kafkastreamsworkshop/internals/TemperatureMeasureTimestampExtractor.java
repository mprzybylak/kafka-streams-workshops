package com.mprzybylak.kafkastreamsworkshop.internals;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

public class TemperatureMeasureTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {

        Object value = record.value();
        if(value instanceof KafkaStreamsTest.TemperatureMeasure) {
            return ((KafkaStreamsTest.TemperatureMeasure)value).timestamp();
        } else {
            throw new ClassCastException();
        }
    }
}