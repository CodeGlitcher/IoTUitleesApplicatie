package iot.meetding.controller;

import jssc.SerialPort;

/**
 * Created by Rob on 18-5-2016.
 *
 */
public class ArduinoPort extends SerialPort {

    public ArduinoPort(String portName){
        super(portName);
    }


}
