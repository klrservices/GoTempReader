package pl.klr.workshops.iot.iotcs;

import oracle.iot.client.DeviceModel;
import oracle.iot.client.device.DirectlyConnectedDevice;
import oracle.iot.client.device.VirtualDevice;
import pl.klr.workshops.iot.sensors.vernier.GoTempProbe;
import pl.klr.workshops.iot.sensors.vernier.GoTempProbeV2;

import javax.usb.UsbException;
import java.util.Timer;
import java.util.TimerTask;

public class IoTCSClient {
    private static final String URN = "urn:vernier:gotemp";
    private static final String TEMPERATURE_ATTRIBUTE = "Temperature";

    public static void main(String[] args) throws Exception {
        DirectlyConnectedDevice dcd = new DirectlyConnectedDevice(args[0], args[1]);

        //Activate if not activated
        if (!dcd.isActivated()) {
            dcd.activate(URN);
        }

        // Setup a virtual device based on device model
        DeviceModel deviceModel = dcd.getDeviceModel(URN);
        VirtualDevice virtualDevice = dcd.createVirtualDevice(dcd.getEndpointId(), deviceModel);

        boolean loopRead = true;

        GoTempProbeV2 probe = new GoTempProbeV2();
        IoTCSPublisher publisher = new IoTCSPublisher(probe, virtualDevice);
        Timer timer = new Timer("GoTempTimer");

        probe.start();
        timer.schedule(publisher, 100, 1000);

        while (loopRead) {
            loopRead = System.in.available() == 0;

            Thread.sleep(100);
        }

        timer.cancel();
        probe.stop();

        dcd.close();
    }

    static class IoTCSPublisher extends TimerTask {
        GoTempProbeV2 probe;
        VirtualDevice virtualDevice;

        public IoTCSPublisher(GoTempProbeV2 probe, VirtualDevice virtualDevice) {
            this.probe = probe;
            this.virtualDevice = virtualDevice;
        }

        @Override
        public void run() {
            Double temperature = null;
            try {
                temperature = probe.poll();

                System.out.println("Temp: "+temperature);

                virtualDevice.set(TEMPERATURE_ATTRIBUTE, temperature);
            } catch (UsbException e) {
                e.printStackTrace();
            }
        }
    }
}
