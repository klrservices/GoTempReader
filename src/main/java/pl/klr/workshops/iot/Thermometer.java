package pl.klr.workshops.iot;

import pl.klr.workshops.iot.data.GoTempMeasurement;
import pl.klr.workshops.iot.kafka.KafkaPublisher;
import pl.klr.workshops.iot.sensors.vernier.GoTempProbe;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Thermometer {
    private static KafkaPublisher kafkaPublisher = new KafkaPublisher();

    public static void main(String[] args) throws Exception {
        String sensorId;
        GoTempProbe goTempProbe = new GoTempProbe();

        if (args.length > 0) {
            sensorId = args[0];
        } else {
            sensorId = Thermometer.getId();
        }

        goTempProbe.start();

        while (true) {
            Double temperature = goTempProbe.poll();

            publishTemperature(new GoTempMeasurement(sensorId,System.currentTimeMillis(), temperature));

            System.out.println("Sensor: "+sensorId+" : " + temperature + "°C");
        }

        //goTempProbe.stop();
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

    public static void publishTemperature(GoTempMeasurement measurement) {
        kafkaPublisher.publishToKafka(measurement);
    }

//    public static int postTemperature(String id, double temperatureCelc) {
//        try {
//            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("wlevs", "welcome1");
//
//            ClientConfig clientConfig = new ClientConfig();
//            clientConfig.register(feature);
//
//            Client client = ClientBuilder.newClient(clientConfig);
//
//            WebTarget webTarget = client.target("http://localhost:9002");
//            WebTarget resourceWebTarget = webTarget.path("localtemp");
//
//
//            Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON);
//            Response response = invocationBuilder.post(Entity.json("{\"id\":\""+id+"\",\"tempcelc\":"+temperatureCelc+"}"));
//            return response.getStatus();
//
//        } catch (Exception e) {
//            return -1;
//        }
//    }

}
