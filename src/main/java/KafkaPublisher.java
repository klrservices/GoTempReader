import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class KafkaPublisher {
    private Properties kafkaProps = new Properties();
    Producer<String, Double> producer = null;

    public KafkaPublisher() {
        super();

        try {
            kafkaProps.load(new FileInputStream("kafka.properties"));
        } catch (IOException e) {
            System.err.println("Exception while reading config file (using default values): "+e.getMessage());
            kafkaProps.put("bootstrap.servers", "localhost:9092");

            kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.DoubleSerializer");
        }

        producer = new KafkaProducer<String, Double>(kafkaProps);
    }

    public void publishToKafka(String deviceId, Double temperatureCelc) {
        ProducerRecord<String, Double> record = new ProducerRecord<String, Double>(kafkaProps.getProperty("topic","thermometer"), deviceId, temperatureCelc);

        producer.send(record);
    }
}
