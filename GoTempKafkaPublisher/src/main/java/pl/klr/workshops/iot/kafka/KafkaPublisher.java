package pl.klr.workshops.iot.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import pl.klr.workshops.iot.data.GoTempMeasurement;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class KafkaPublisher {
    private Properties kafkaProps = new Properties();
    Producer<String, GoTempMeasurement> producer = null;

    public KafkaPublisher() {
        super();

        try {
            kafkaProps.load(new FileInputStream("kafka.properties"));
        } catch (IOException e) {
            System.err.println("Exception while reading config file (using default values): "+e.getMessage());
            kafkaProps.put("bootstrap.servers", "localhost:9092");

            kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            kafkaProps.put("value.serializer", "pl.klr.workshops.iot.serialization.GoTempSerializer");
        }

        producer = new KafkaProducer<String, GoTempMeasurement>(kafkaProps);
    }

    public void publishToKafka(GoTempMeasurement measurement) {
        ProducerRecord<String, GoTempMeasurement> record = new ProducerRecord<>(kafkaProps.getProperty("topic","thermometer"), measurement.getDeviceId(), measurement);

        producer.send(record);
    }
}
