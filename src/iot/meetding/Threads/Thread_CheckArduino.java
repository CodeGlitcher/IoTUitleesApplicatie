package iot.meetding.threads;

import iot.meetding.ArduinoSerialPort;
import iot.meetding.model.IoTmodel;
import iot.meetding.view.beans.WindowDataReadArduino;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by Rob on 20-5-2016.
 * A thread for checking if a comport is connected to our Arduino
 *
 */
public class Thread_CheckArduino extends Thread implements SerialPortEventListener {
    // max timeout for arduino to answer
    private final int TIME_OUT = 10000;
    // the arduino needs some time to boot, give it this much time
    private final int ARDUINO_BOOT_TIME = 3000;

    private boolean isArduino = false;

    private WindowDataReadArduino data;
    private ArduinoSerialPort port;

    public Thread_CheckArduino(ArduinoSerialPort port, WindowDataReadArduino data) throws SerialPortException {
        // if port is not open open it.
        if (!port.isOpened()) {
            port.openPort();

        }

        this.data = data;
        this.port = port;
    }

    @Override
    public void run() {
        super.run();
        try {
            try {
                port.addEventListener(this);
                sleep(ARDUINO_BOOT_TIME);
                data.appendLogData("Verstuur is klimaatscanner bericht.");
                port.writeString(ArduinoSerialPort.MESSAGE_IS_ARDUINO);
                sleep(TIME_OUT);
            } catch (SerialPortException exception) {
                data.appendLogData("Serial fout. herstart de applicatie en sluit de scanner opnieuw aan.");
            } catch (InterruptedException interrupt) {
                data.appendLogData("Antwoordt ontvangen van de serial poort");
            }

            try {
                if (isArduino) {
                    IoTmodel.getInstance().addPort(port);
                    data.appendLogData("Klimaatscanner gevonden!");
                    port.removeEventListener();
                } else {
                    data.appendLogData("Geen klimaatscanner, verbingen verbreken.");
                    port.closePort();
                }
            } catch (SerialPortException e) {
                // null
            } // make sure the thread is closed
        } finally {
            IoTmodel.getInstance().closeThread();
        }
    }

// event handler for receiving data
    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String receivedData = port.readString(event.getEventValue());
                receivedData = receivedData.trim(); // remove any spaces
                data.appendLogData(receivedData);
                isArduino = receivedData.equals(ArduinoSerialPort.ANSWER_IS_ARDUINO) || isArduino;
                if (isArduino) {
                    // wake up thread
                    interrupt();
                }
            } catch (SerialPortException ex) {
                data.appendLogData("Fout in poort: " + port.getPortName());
            }
        }
    }
}
