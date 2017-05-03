package pl.klr.workshops.iot.data;

public class Measurement {
    private String deviceId;
    private long measurementTime;
    private double temperature;

    public Measurement(String deviceId, long measurementTime, double temperature) {
        this.deviceId = deviceId;
        this.measurementTime = measurementTime;
        this.temperature = temperature;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public long getMeasurementTime() {
        return measurementTime;
    }

    public double getTemperature() {
        return temperature;
    }
}
