package pl.klr.workshops.iot.utils;

public class GoTempMeasurement {
    private String deviceId;
    private long measurementTime;
    private double temperature;

    public GoTempMeasurement(String deviceId, long measurementTime, double temperature) {
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
