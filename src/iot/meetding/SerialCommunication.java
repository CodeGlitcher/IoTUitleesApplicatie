package iot.meetding;

import jssc.SerialPort;

import java.util.concurrent.Semaphore;

/**
 * Created by Rob on 20-5-2016.
 */
public class SerialCommunication {

    private Semaphore lock1;
    private SerialPort currentPort;

    public SerialCommunication(){
        lock1 = new Semaphore(1);
    }

    public void setPort() throws InterruptedException {

    }




}
