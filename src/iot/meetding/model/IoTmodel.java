package iot.meetding.model;

import iot.meetding.Logger;
import jssc.SerialPort;
import jssc.SerialPortList;

import java.util.ArrayList;

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


    public ArrayList<String> getComPorts(){
        ArrayList<String> result = new ArrayList<>();
        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames) {
            Logger.log(portName);
            result.add(portName);
        }
        return result;
    }
}
