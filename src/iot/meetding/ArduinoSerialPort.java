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
    public static final String ANSWER_READ_DATA = "$1";

    public static final String MESSAGE_READ_CONFIG =  "#2";
    public static final String ANSWER_READ_CONFIG = "$2";

    public static final String MESSAGE_SEND_CONFIG =  "#3";
    public static final String ANSWER_SEND_CONFIG = "$";

    public static final String MESSAGE_SEND_TIME =  "#4";
    public static final String ANSWER_SEND_TIME = "$";


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
