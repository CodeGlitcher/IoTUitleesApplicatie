package iot.meetding.model;

import iot.meetding.Logger;
import jssc.SerialPort;
import jssc.SerialPortList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rob on 18-5-2016.
 *
 */
public class IoTmodel {


    private static IoTmodel model;
    private IoTmodel(){

    }

    public static IoTmodel getInstance(){
        if(model == null){
            model = new IoTmodel();
        }
        return model;
    }


    public HashSet<String> getComPorts(){
        HashSet<String> result = new HashSet<>();
        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames) {
            Logger.log(portName);
            result.add(portName);
        }
        return result;
    }
}
