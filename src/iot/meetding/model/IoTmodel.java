package iot.meetding.model;

import iot.meetding.ArduinoSerialPort;
import iot.meetding.Logger;
import iot.meetding.Threads.Thread_CheckArduino;
import iot.meetding.Threads.Thread_ReadData;
import iot.meetding.view.beans.WindowDataReadArduino;
import jssc.*;

import java.util.*;

/**
 * Created by Rob on 18-5-2016.
 *
 */
public class IoTmodel extends Observable {

    private static IoTmodel model;

    private TreeMap<String,ArduinoSerialPort> ports;
    private  Thread t;
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

    /**
     * update Com ports
     * @param data
     */
    public synchronized void updateComPorts(WindowDataReadArduino data) {
        if(threadCounter != 0){
            return;
        }
        HashSet<String> result = new HashSet<>();
        // get a simple list of all com ports on the system
        String[] portNames = SerialPortList.getPortNames();
        Thread t;
        removeAllPorts();
        // check every port for a arduino
        for (String portName : portNames) {
            if (result.add(portName)) { // make sure only 1 thread per comport
                try {
                    t = new Thread_CheckArduino(new ArduinoSerialPort(portName),data);
                    t.start();
                    threadCounter++;
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Close com port en forget it.
     * @param portName the port name
     */
    private synchronized void removePort(String portName) {
        ArduinoSerialPort port = ports.remove(portName);
        try {
            port.closePort();
        } catch (SerialPortException e) {
            Logger.log("Closing port failed");
            Logger.log(e.getMessage());
        } catch (NullPointerException e) {
            Logger.log(String.format("Key does not exist: %1s", portName));
            Logger.log(e.getMessage());
        }
    }

    /**
     * Remove all com ports
     */
    public void removeAllPorts(){
        for(String port : ports.keySet()){
            removePort(port);
        }
    }

    /**
     * Add a valid comport to the model
     * @param newPort the port
     */
    public synchronized void addPort(ArduinoSerialPort newPort) {
        // something bad happend ports must unique
        if (ports.containsKey(newPort.getPortName())) {
            removePort(newPort.getPortName());
        }
        ports.put(newPort.getPortName(), newPort);
        setChanged();
        notifyObservers(newPort.getPortName());
    }

    public ArduinoSerialPort getPort(String selectedItem) {
        return ports.get(selectedItem);
    }

    public synchronized void closeThread(){
        threadCounter--;
    }


    /**
     * Start a thread for reading data from the arduino.
     * This only starts if no other thread is running.
     * @param comPort
     * @param data
     */
    public void startReadData(String comPort, WindowDataReadArduino data) {
        if(t != null && t.isAlive()){
            data.appendLogData("Process still running");
            return;
        }
        try {
            t = new Thread_ReadData(model.getPort(comPort), data);
            t.start();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }
}


