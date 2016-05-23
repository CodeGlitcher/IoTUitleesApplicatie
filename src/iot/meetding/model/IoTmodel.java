package iot.meetding.model;

import iot.meetding.ArduinoSerialPort;
import iot.meetding.Logger;
import iot.meetding.Thread_CheckArduino;
import jssc.*;

import java.util.*;

/**
 * Created by Rob on 18-5-2016.
 *
 */
public class IoTmodel extends Observable {

    private static IoTmodel model;

    private TreeMap<String,ArduinoSerialPort> ports;

    private int threadCounter = 0;
    private IoTmodel(){

        ports = new TreeMap<>();
    }

    public static IoTmodel getInstance(){
        if(model == null){
            model = new IoTmodel();
        }
        return model;
    }


    public synchronized void updateComPorts() {
        if(threadCounter != 0){
            return;
        }
        HashSet<String> result = new HashSet<>();
        String[] portNames = SerialPortList.getPortNames();
        Thread t;
        removeAllPorts();
        for (String portName : portNames) {
            if (result.add(portName)) { // make sure only 1 thread per comport
                try {
                    t = new Thread_CheckArduino(new ArduinoSerialPort(portName));
                    t.start();
                    threadCounter++;
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized void removePort(String portName) {
        ArduinoSerialPort port = ports.remove(portName);
        try {
            port.closePort();
        } catch (SerialPortException e) {
           Logger.log("Closing port failed");
        }
    }
    private void removeAllPorts(){
        for(String port : ports.keySet()){
            removePort(port);
        }
    }

    public synchronized void addPort(ArduinoSerialPort newPort) {
        // something bad happend ports must unique
        if (ports.containsKey(newPort.getPortName())) {
            removePort(newPort.getPortName());
        }
        threadCounter--;
        ports.put(newPort.getPortName(), newPort);
        System.out.println("test");
        setChanged();
        notifyObservers(newPort.getPortName());
    }
}


