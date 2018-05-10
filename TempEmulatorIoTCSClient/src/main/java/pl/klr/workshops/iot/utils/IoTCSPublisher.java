package pl.klr.workshops.iot.utils;

import oracle.iot.client.DeviceModel;
import oracle.iot.client.device.DirectlyConnectedDevice;
import oracle.iot.client.device.VirtualDevice;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class IoTCSPublisher {
    private static final String URN = "urn:vernier:gotemp";
    private static final String TEMPERATURE_ATTRIBUTE = "Temperature";
    private DirectlyConnectedDevice dcd;
    private DeviceModel deviceModel;
    private VirtualDevice virtualDevice;

    public IoTCSPublisher() {
    }


    public void init(String configFilePath, String configFilePassword) throws IOException, GeneralSecurityException {
        dcd = new DirectlyConnectedDevice(configFilePath, configFilePassword);

        //Activate if not activated
        if (!dcd.isActivated()) {
            dcd.activate(URN);
        }

        // Setup a virtual device based on device model

        deviceModel = dcd.getDeviceModel(URN);

        virtualDevice = dcd.createVirtualDevice(dcd.getEndpointId(), deviceModel);
    }

    public void close() throws IOException {
        dcd.close();
    }

    public void publish(GoTempMeasurement goTempMeasurement) {
        System.out.println("Temp: "+goTempMeasurement.getTemperature());

        virtualDevice.set(TEMPERATURE_ATTRIBUTE, goTempMeasurement.getTemperature());
    }
}
