import javax.usb.*;
import javax.usb.util.UsbUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

public class Thermometer {
    public final static int VERNIER_VENDOR_ID = 0x8F7;
    public final static int GOTEMP_PRODUCT_ID = 2;
    private static KafkaPublisher kafkaPublisher = new KafkaPublisher();

    public static void main(String[] args) throws UsbException, IOException {
        String sensorId;

        if (args.length > 0) {
            sensorId = args[0];
        } else {
            sensorId = Thermometer.getId();
        }

        UsbDevice probe = findProbe();
        if (probe == null) {
            System.err.println("No Go!Temp probe attached.");
            return;
        }

        UsbConfiguration config = probe.getActiveUsbConfiguration();
        UsbInterface theInterface = config.getUsbInterface((byte) 0);
        theInterface.claim(new UsbInterfacePolicy() {
            public boolean forceClaim(UsbInterface usbInterface) {
                return true;
            }
        });
        UsbEndpoint endpoint = (UsbEndpoint) theInterface.getUsbEndpoints().get(0);
        UsbPipe pipe = endpoint.getUsbPipe();

        UsbIrp irp = pipe.createUsbIrp();
        byte[] data = new byte[8];
        irp.setData(data);
        pipe.open();

        outer:
        while (true) {
            pipe.syncSubmit(irp);
            int numberOfMeasurements = data[0];
            for (int i = 0; i < numberOfMeasurements; i++) {
                int result = UsbUtil.toShort(data[2 * i + 3], data[2 * i + 2]);
                int sequenceNumber = UsbUtil.unsignedInt(data[1]);
                double temperature = result / 128.0;
                if (temperature > 110.0) {
                    System.err.println("Maximum accurate temperature exceeded.");
                    break outer;
                } else if (temperature < -10) {
                    System.err.println("Minimum accurate temperature exceeded.");
                    break outer;
                }

//                int status = postTemperature(sensorId, temperature);
                publishTemperature(sensorId,temperature);

                System.out.println("Sensor: "+sensorId+" measurement " + sequenceNumber + ": " + temperature + "°C");

            }
            irp.setComplete(false);
        }
        pipe.abortAllSubmissions();
        pipe.close();
        theInterface.release();
    }

    private static UsbDevice findProbe() throws UsbException, IOException {
        UsbServices services = UsbHostManager.getUsbServices();
        UsbHub root = services.getRootUsbHub();
        return searchDevices(root);
    }

    private static UsbDevice searchDevices(UsbHub hub) throws UsbException, IOException {
        List devices = hub.getAttachedUsbDevices();
        Iterator iterator = devices.iterator();
        while (iterator.hasNext()) {
            UsbDevice device = (UsbDevice) iterator.next();
            UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();
            int manufacturerCode = descriptor.idVendor();
            int productCode = descriptor.idProduct();
            if (manufacturerCode == VERNIER_VENDOR_ID && productCode == GOTEMP_PRODUCT_ID) {
                return device;
            } else if (device.isUsbHub()) {
                UsbDevice found = searchDevices((UsbHub) device);
                if (found != null)
                    return found;
            }
        }
        return null;
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

    public static void publishTemperature(String id, double temperatureCelc) {
        kafkaPublisher.publishToKafka(id, temperatureCelc);
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
