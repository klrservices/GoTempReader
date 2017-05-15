package pl.klr.workshops.iot;

import pl.klr.workshops.iot.data.GoTempMeasurement;
import pl.klr.workshops.iot.kafka.KafkaPublisher;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TemperatureMockup {
    private KafkaPublisher publisher = null;
    private double currentTemperature = 26.0;

    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public TemperatureMockup() {
        super();
    }

    public TemperatureMockup(KafkaPublisher publisher) {
        super();
        this.publisher = publisher;
    }

    public void start(final String id) {
        Thread thermometer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    double temperature = getCurrentTemperature();
//                    int status = Thermometer.postTemperature(id, temperature);
                    publisher.publishToKafka(new GoTempMeasurement(id, System.currentTimeMillis(), temperature));
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
        final KafkaPublisher publisher = new KafkaPublisher();
        final TemperatureMockup mockup = new TemperatureMockup(publisher);

        Thread thermometer = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    double temperature = mockup.getCurrentTemperature();
                    publisher.publishToKafka(new GoTempMeasurement(id, System.currentTimeMillis(), temperature));
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
        }
    }
}
