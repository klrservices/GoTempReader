package pl.klr.workshops.iot.sensors.vernier;

import javax.usb.*;
import javax.usb.util.UsbUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GoTempProbe {
    public final static int VERNIER_VENDOR_ID = 0x8F7;
    public final static int GOTEMP_PRODUCT_ID = 2;

    private UsbDevice probe;
    private UsbConfiguration config;
    private UsbInterface theInterface;
    private UsbEndpoint endpoint;
    private UsbPipe pipe;
    private UsbIrp irp;
    private byte[] data = new byte[8];

    public GoTempProbe() {
        super();
    }

    public void start() throws Exception {
        probe = findProbe();
        if (probe == null) {
            throw new Exception("No Go!Temp probe attached.");
        }

        config = probe.getActiveUsbConfiguration();
        theInterface = config.getUsbInterface((byte) 0);
        theInterface.claim(new UsbInterfacePolicy() {
            public boolean forceClaim(UsbInterface usbInterface) {
                return true;
            }
        });
        endpoint = (UsbEndpoint) theInterface.getUsbEndpoints().get(0);
        pipe = endpoint.getUsbPipe();

        irp = pipe.createUsbIrp();

        irp.setData(data);
        pipe.open();
    }

    public void stop() throws UsbException {
        pipe.abortAllSubmissions();
        pipe.close();
        theInterface.release();
    }

    public Double poll() throws UsbException {
        //List<Double> measurements = new ArrayList<>();
        double tempSum = 0d;

        pipe.syncSubmit(irp);
        int numberOfMeasurements = data[0];
        int sequenceNumber = UsbUtil.unsignedInt(data[1]);

        for (int i = 0; i < numberOfMeasurements; i++) {
            int result = UsbUtil.toShort(data[2 * i + 3], data[2 * i + 2]);

            double temperature = result / 128.0;

            if (temperature > 110.0) {
                System.err.println("Maximum accurate temperature exceeded.");
            } else if (temperature < -10) {
                System.err.println("Minimum accurate temperature exceeded.");
            }

            tempSum += temperature;
        }

        irp.setComplete(false);

        return tempSum/numberOfMeasurements;
    }

    private UsbDevice findProbe() throws UsbException, IOException {
        UsbServices services = UsbHostManager.getUsbServices();
        UsbHub root = services.getRootUsbHub();
        return searchDevices(root);
    }

    private UsbDevice searchDevices(UsbHub hub) throws UsbException, IOException {
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

    public static void main(String[] args) {
        GoTempProbe goTempProbe = new GoTempProbe();

        try {
            goTempProbe.start();

            Double temp = goTempProbe.poll();

            System.out.println(temp);

            goTempProbe.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
