package pl.klr.workshops.iot.sensors.vernier;

import javax.usb.UsbException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.DoubleAccumulator;

public class GoTempProbeV2 {
    private GoTempProbe probe;
    private Actuator actuator;

    private Timer timer;
    private DoubleAccumulator avgAcc;

    public GoTempProbeV2() {
        avgAcc = new DoubleAccumulator(
                /*(left, right) -> ((left == Double.NEGATIVE_INFINITY ? right : left) + right) / 2
                , Double.NEGATIVE_INFINITY*/
                (left, right) -> right, 0d
        );

        probe = new GoTempProbe();
        actuator = new Actuator(probe, avgAcc);
        timer = new Timer("Actuator");
    }

    public void start() throws Exception {
        probe.start();
        timer.schedule(actuator,100, 100);
    }

    public void stop() throws UsbException {
        timer.cancel();
        probe.stop();
    }

    public Double poll() throws UsbException {
        return avgAcc.doubleValue();
    }

    private static class Actuator extends TimerTask {
        private final GoTempProbe probe;
        private final DoubleAccumulator accumulator;

        public Actuator(GoTempProbe probe, DoubleAccumulator accumulator) {
            this.probe = probe;
            this.accumulator = accumulator;
        }

        @Override
        public void run() {
            try {
                Double actualTemperature = probe.poll();
                accumulator.accumulate(actualTemperature);
            } catch (UsbException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        GoTempProbeV2 goTempProbe = new GoTempProbeV2();
        boolean loopRead = true;

        try {
            goTempProbe.start();

            while (loopRead) {
                loopRead = System.in.available() == 0;

                Thread.sleep(2000);

                Double temp = goTempProbe.poll();

                System.out.println("Measured temperature: "+temp);
            }

            goTempProbe.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
