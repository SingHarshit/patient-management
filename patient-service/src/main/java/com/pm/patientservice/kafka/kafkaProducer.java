package com.pm.patientservice.kafka;

import com.pm.patientservice.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

@Service
public class kafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(kafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public kafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {

        log.info(">>> Preparing event for Kafka. patientId={}, email={}",
                patient.getId(), patient.getEmail());

        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();

        try {
            kafkaTemplate.send("patient-event-topic", event.toByteArray())
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("!!! Kafka SEND FAILED: {}", ex.getMessage(), ex);
                        } else {
                            log.info(">>> Kafka SEND SUCCESS. Topic={}, Partition={}, Offset={}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("!!! Exception while sending Kafka event: {}", e.getMessage(), e);
        }
    }
}
