package iot.meetding.threads;

import iot.meetding.ArduinoSerialPort;
import iot.meetding.view.beans.WindowDataReadArduino;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.IOException;

/**
 * Created by Rob on 21-6-2016.
 *
 * A thread that removes the data file from the Arduino
 */
public class Thread_RemoveData extends Thread implements SerialPortEventListener {

    private WindowDataReadArduino data;
    private ArduinoSerialPort port;
    private int timeout = 10000;
    boolean succes = false;
    public Thread_RemoveData( ArduinoSerialPort port, WindowDataReadArduino data) throws SerialPortException {
        if(!port.isOpened()){
            throw new SerialPortException(port.getPortName(), "Thread remove data constructor", SerialPortException.TYPE_PORT_NOT_OPENED);
        }
        this.port = port;
        this.data = data;
    }

    @Override
    public void run() {
        super.run();
        data.setFileSize(3);
        data.resetProgress();

        try {
            data.appendLogData("Data opvragen van klimaatscanner");
            port.addEventListener(this);

            port.writeString(ArduinoSerialPort.MESSAGE_REMOVE_DATA);

            try{
                sleep(timeout);
            } catch(InterruptedException e){
                // done
            }
            if(succes){
                data.appendLogData("Verwijderen gelukt");
            } else {
                data.appendLogData("Verwijderen mislukt");
            }

        } catch (SerialPortException e) {
            data.appendLogData("Verwijderen mislukt");
            e.printStackTrace();
        }
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                // read bytes from serial
                byte[] buff = port.readBytes(event.getEventValue());
                // check if we need to add header information to the output file
                String message = new String(buff);
                System.out.print(message);
                if(message.equals(ArduinoSerialPort.ANSWER_REMOVE_DATA)) {
                    succes = true;
                    interrupt();
                }

            } catch (SerialPortException ex) {
                data.appendLogData("Uitlezen mislukt, verwijder data.csv en probeer opnieuw");
                interrupt();
            }
        }
    }

}
