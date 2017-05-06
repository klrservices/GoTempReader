package pl.klr.workshops.iot.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import pl.klr.workshops.iot.data.GoTempMeasurement;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import static pl.klr.workshops.iot.serialization.GoTempSerializer.SerializationHelper.from;

public class GoTempSerializer implements Closeable, AutoCloseable, Serializer<GoTempMeasurement>, Deserializer<GoTempMeasurement> {
    private ObjectMapper mapper;

    public GoTempSerializer() {
        this(new ObjectMapper());
    }

    public GoTempSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static GoTempSerializer defaultConfig() {
        return new GoTempSerializer(new ObjectMapper());
    }

    public static GoTempSerializer smileConfig() {
        return new GoTempSerializer(new ObjectMapper(new SmileFactory()));
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
    public byte[] serialize(String s, GoTempMeasurement measurement) {
        try {
            return mapper.writeValueAsBytes(from(measurement));
        }
        catch(JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public GoTempMeasurement deserialize(String s, byte[] bytes) {
        try {
            return mapper.readValue(bytes, SerializationHelper.class).to();
        }
        catch(IOException e) {
            throw new IllegalArgumentException(e);
        }
    }


    @Override
    public void close() {
        //mapper = null;
    }

    public static class SerializationHelper {
        public String deviceId;
        public long measurementTime;
        public double temperature;

        public static SerializationHelper from(GoTempMeasurement measurement) {
            SerializationHelper helper = new SerializationHelper();
            helper.deviceId = measurement.getDeviceId();
            helper.measurementTime = measurement.getMeasurementTime();
            helper.temperature = measurement.getTemperature();

            return helper;
        }

        public GoTempMeasurement to() {
            return new GoTempMeasurement(deviceId, measurementTime, temperature);
        }
    }
}
