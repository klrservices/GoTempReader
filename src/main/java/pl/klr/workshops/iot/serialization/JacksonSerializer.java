package pl.klr.workshops.iot.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import pl.klr.workshops.iot.data.Measurement;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import static pl.klr.workshops.iot.serialization.JacksonSerializer.SerializationHelper.from;

public class JacksonSerializer  implements Closeable, AutoCloseable, Serializer<Measurement>, Deserializer<Measurement> {
    private ObjectMapper mapper;

    public JacksonSerializer() {
        this(null);
    }

    public JacksonSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static JacksonSerializer defaultConfig() {
        return new JacksonSerializer(new ObjectMapper());
    }

    public static JacksonSerializer smileConfig() {
        return new JacksonSerializer(new ObjectMapper(new SmileFactory()));
    }

    @Override
    public void configure(Map<String, ?> map, boolean b) {
        if(mapper == null) {
            if("true".equals(map.get("value.serializer.jackson.smile"))) {
                mapper = new ObjectMapper(new SmileFactory());
            }
            else {
                mapper = new ObjectMapper();
            }
        }
    }

    @Override
    public byte[] serialize(String s, Measurement measurement) {
        try {
            return mapper.writeValueAsBytes(from(measurement));
        }
        catch(JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Measurement deserialize(String s, byte[] bytes) {
        try {
            return mapper.readValue(bytes, SerializationHelper.class).to();
        }
        catch(IOException e) {
            throw new IllegalArgumentException(e);
        }
    }


    @Override
    public void close() {
        mapper = null;
    }

    public static class SerializationHelper {
        public String deviceId;
        public long measurementTime;
        public double temperature;

        public static SerializationHelper from(Measurement measurement) {
            SerializationHelper helper = new SerializationHelper();
            helper.deviceId = measurement.getDeviceId();
            helper.measurementTime = measurement.getMeasurementTime();
            helper.temperature = measurement.getTemperature();

            return helper;
        }

        public Measurement to() {
            return new Measurement(deviceId, measurementTime, temperature);
        }
    }
}
