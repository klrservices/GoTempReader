package pl.klr.workshops.iot.iotcs;

import oracle.iot.client.DeviceModel;
import oracle.iot.client.device.DirectlyConnectedDevice;
import oracle.iot.client.device.VirtualDevice;
import pl.klr.workshops.iot.sensors.vernier.GoTempProbe;

import java.util.Date;

public class IoTCSClient {
    private static final String URN = "urn:vernier:gotemp";
    private static final String MEASUREMENT_TIME_ATTRIBUTE = "MeasurementTime";
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

        GoTempProbe probe = new GoTempProbe();

        probe.start();

        Double temperature = probe.poll();

        virtualDevice.set(MEASUREMENT_TIME_ATTRIBUTE, new Date());
        virtualDevice.set(TEMPERATURE_ATTRIBUTE, temperature);

        probe.stop();

        dcd.close();
    }
}
