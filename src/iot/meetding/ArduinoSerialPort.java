package iot.meetding;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by Rob on 20-5-2016.
 * Custom serial port class for communication with arduino
 */
public class ArduinoSerialPort extends SerialPort {

    public static final String MESSAGE_IS_ARDUINO =  "#0";
    public static final String ANSWER_IS_ARDUINO = "$0";


    public static final String MESSAGE_READ_DATA =  "#1";
    public static final String ANSWER_READ_END = "$1";


    public static final byte DELIMITER = (byte) '\n';

    public ArduinoSerialPort(String portName) {
        super(portName);
    }

    @Override
    public boolean openPort() throws SerialPortException {
        boolean result = super.openPort();
        if(result){
            setParams(BAUDRATE_38400, DATABITS_8, STOPBITS_1, PARITY_NONE);
        }
        return result;
    }




}
