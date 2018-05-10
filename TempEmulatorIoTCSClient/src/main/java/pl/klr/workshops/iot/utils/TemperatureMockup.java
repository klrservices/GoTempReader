package pl.klr.workshops.iot.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

public class TemperatureMockup {
    private double currentTemperature = 26.0;
    private IoTCSPublisher publisher;

    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public TemperatureMockup() {
        super();
    }

    public TemperatureMockup(IoTCSPublisher publisher) {
        this.publisher = publisher;
    }

    public void start(final String id) {
        Thread thermometer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    double temperature = getCurrentTemperature();
//                    int status = Thermometer.postTemperature(id, temperature);
                    publisher.publish(new GoTempMeasurement(id, System.currentTimeMillis(), temperature));
                    System.out.println("Measurement : " + temperature + "°C");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });

        thermometer.start();        
    }

    public static String getId() {
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (ip != null) {
            return ip.toString();
        } else {
            return "unknown";
        }
    }

    public static void main(String[] args) {
        final String id = getId();
        IoTCSPublisher publisher = new IoTCSPublisher();
        try {
            publisher.init(args[0], args[1]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        final TemperatureMockup mockup = new TemperatureMockup(publisher);

        Thread thermometer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    double temperature = mockup.getCurrentTemperature();
                    publisher.publish(new GoTempMeasurement(id, System.currentTimeMillis(), temperature));
                    System.out.println("Measurement : " + temperature + "°C");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });

        thermometer.start();

        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            try {
                publisher.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
