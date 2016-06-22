package iot.meetding.threads;

import iot.meetding.ArduinoSerialPort;
import iot.meetding.model.IoTmodel;
import iot.meetding.view.beans.WindowDataReadArduino;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by Rob on 20-5-2016.
 * A thread for checking if a comport is connected to our Arduino
 *
 */
public class Thread_CheckArduino extends Thread implements SerialPortEventListener {
    // max timeout for arduino to answer
    private final int TIME_OUT = 5000;
    // the arduino needs some time to boot, give it this much time
    private final int ARDUINO_BOOT_TIME = 3000;

    private boolean isArduino = false;

    private WindowDataReadArduino data;
    private ArduinoSerialPort port;

    public Thread_CheckArduino(ArduinoSerialPort port, WindowDataReadArduino data)  {


        this.data = data;
        this.port = port;
    }


    private long startTime;

    public long getStartTime(){
        return startTime;
    }

    public void close(){
        try {
            port.purgePort(SerialPort.PURGE_RXCLEAR);
            port.purgePort(SerialPort.PURGE_TXCLEAR);
            port.closePort();
        } catch (SerialPortException e) {
            System.err.print(e.getMessage());
        }
    }



    @Override
    public void run() {
        startTime = System.currentTimeMillis();

        super.run();
        try {

            // if port is not open open it.
            if (port.isOpened()) {
                port.closePort();
            }
            port.openPort();

            try {
                port.addEventListener(this);
                sleep(ARDUINO_BOOT_TIME);
                data.appendLogData(String.format("%s: Verstuur is klimaatscanner bericht.", port.getPortName()));
                port.writeString(ArduinoSerialPort.MESSAGE_IS_ARDUINO);
                sleep(TIME_OUT);
            } catch (SerialPortException exception) {
                data.appendLogData(String.format("%s: Serial fout. herstart de applicatie en sluit de scanner opnieuw aan.", port.getPortName()));
            } catch (InterruptedException interrupt) {
                data.appendLogData(String.format("%s: Antwoordt ontvangen van de serial poort", port.getPortName()));
            }
            try {
                if (isArduino) {
                    IoTmodel.getInstance().addPort(port);
                    data.appendLogData(String.format("%s: Klimaatscanner gevonden!", port.getPortName()));
                    port.removeEventListener();
                } else {
                    data.appendLogData(String.format("%s: Geen klimaatscanner, verbingen verbreken.", port.getPortName()));
                    port.closePort();
                }
            } catch (SerialPortException e) {
                // null
            } // make sure the thread is closed
        } catch (SerialPortException e) {
            // fail
            data.appendLogData(String.format("%s: Geen klimaatscanner, verbingen verbreken.", port.getPortName()));
        }
    }

// event handler for receiving data
    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String receivedData = port.readString(event.getEventValue());

                receivedData = receivedData.trim(); // remove any spaces
                data.appendLogData(receivedData);
                isArduino = receivedData.equals(ArduinoSerialPort.ANSWER_IS_ARDUINO) || isArduino;
                if (isArduino) {
                    // wake up thread
                    interrupt();
                }
            } catch (SerialPortException ex) {
                data.appendLogData(String.format("%s: Fout in poort", port.getPortName()));
            }
        }
    }
}
