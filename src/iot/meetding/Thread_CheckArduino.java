package iot.meetding;

import iot.meetding.model.IoTmodel;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by Rob on 20-5-2016.
 *
 */
public class Thread_CheckArduino extends Thread implements SerialPortEventListener {
    private final int TIME_OUT = 15000;
    private final int ARDUINO_BOOT_TIME = 2000;
    private boolean isArduino = false;
    private ArduinoSerialPort port;
    public Thread_CheckArduino(ArduinoSerialPort port) throws SerialPortException {
        if(!port.isOpened()){
            port.openPort();

        }
        this.port = port;
    }

    @Override
    public void run() {
        super.run();
        try {
            sleep(ARDUINO_BOOT_TIME);
            Logger.log("Sending request");
            port.addEventListener(this);
            port.writeInt(ArduinoSerialPort.MESSAGE_IS_ARDUINO);
            port.writeByte(ArduinoSerialPort.DELIMITER);
            sleep(TIME_OUT);

        } catch (SerialPortException exception) {
            Logger.log(exception.getMessage());
        } catch (InterruptedException interrupt) {
            Logger.log("Got answer from arduino");
        }

        try {
            if (isArduino) {
                IoTmodel.getInstance().addPort(port);
                port.removeEventListener();
            } else {
                System.out.println("CLose");
                port.closePort();
            }
        } catch (SerialPortException e) {
            Logger.log(e.getMessage());
        }
    }


    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String receivedData = port.readString(event.getEventValue());
                receivedData = receivedData.trim();
                System.out.println(receivedData);
                isArduino = receivedData.equals(ArduinoSerialPort.ANSWER_IS_ARDUINO +"");
                interrupt();
            } catch (SerialPortException ex) {
                Logger.log("Error in receiving data from " + port.getPortName()+": " + ex.getMessage());
            }
        }
    }
}
