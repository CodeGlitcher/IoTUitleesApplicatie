package iot.meetding;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by Rob on 20-5-2016.
 *
 */
public class ArduinoSerialPort extends SerialPort {

    public static final int MESSAGE_IS_ARDUINO =  100;
    public static final int ANSWER_IS_ARDUINO = 101;
    public static final byte DELIMITER = (byte) '\n';

    public ArduinoSerialPort(String portName) {
        super(portName);
    }

    @Override
    public boolean openPort() throws SerialPortException {
        boolean result = super.openPort();
        if(result){
            setParams(BAUDRATE_115200, DATABITS_8, STOPBITS_1, PARITY_NONE);
        }
        return result;
    }




}
