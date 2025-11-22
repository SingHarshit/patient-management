package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class kafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(kafkaConsumer.class);

    @KafkaListener(
            topics = "patient-event-topic",
            groupId = "analytics-service-v2",
            containerFactory = "kafkaListenerContainerFactory"   // ⭐ CRITICAL FIX
    )
    public void consumeEvent(byte[] event) {

        if (event == null) {
            log.error("❌ Received null event from Kafka");
            return;
        }

        // Log raw bytes — proves listener is firing
        log.info("📥 Kafka Listener triggered. Received {} bytes.", event.length);

        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);

            log.info("✅ Parsed PatientEvent: id={}, name={}, email={}",
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEmail()
            );

        } catch (InvalidProtocolBufferException e) {
            log.error("❌ Error deserializing PatientEvent: {}", e.getMessage(), e);
        }
    }
}
