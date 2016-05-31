package iot.meetding.Threads;

import iot.meetding.ArduinoSerialPort;
import iot.meetding.Logger;
import iot.meetding.model.IoTmodel;
import iot.meetding.view.beans.WindowDataReadArduino;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by Rob on 20-5-2016.
 *
 */
public class Thread_CheckArduino extends Thread implements SerialPortEventListener {
    private final int TIME_OUT = 10000;
    private final int ARDUINO_BOOT_TIME = 3000;
    private boolean isArduino = false;
    private WindowDataReadArduino data;
    private ArduinoSerialPort port;

    public Thread_CheckArduino(ArduinoSerialPort port, WindowDataReadArduino data) throws SerialPortException {
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
            port.addEventListener(this);
            sleep(ARDUINO_BOOT_TIME);
            data.appendLogData("Sending request");
            port.writeString(ArduinoSerialPort.MESSAGE_IS_ARDUINO);
            sleep(TIME_OUT);
        } catch (SerialPortException exception) {
            Logger.log(exception.getMessage());
        } catch (InterruptedException interrupt) {
            data.appendLogData("Got answer from com port");
        }

        try {
            if (isArduino) {
                IoTmodel.getInstance().addPort(port);
                data.appendLogData("Arduino found");
                port.removeEventListener();
            } else {
                data.appendLogData("Close port");
                port.closePort();
            }
        } catch (SerialPortException e) {
            Logger.log(e.getMessage());
        } finally {
            IoTmodel.getInstance().closeThread();
        }
    }


    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String receivedData = port.readString(event.getEventValue());
                receivedData = receivedData.trim();
                data.appendLogData(receivedData);
                isArduino = receivedData.equals(ArduinoSerialPort.ANSWER_IS_ARDUINO) || isArduino;
                if (isArduino) {
                    Logger.log("Arduino found on: " + port.getPortName());
                    interrupt();
                }
            } catch (SerialPortException ex) {
                Logger.log("Error in receiving data from " + port.getPortName() + ": " + ex.getMessage());
            }
        }
    }
}
