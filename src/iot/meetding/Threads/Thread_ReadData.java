package iot.meetding.threads;


import iot.meetding.ArduinoSerialPort;
import iot.meetding.model.IoTmodel;
import iot.meetding.view.beans.WindowDataReadArduino;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;


import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Rob on 23-5-2016.
 * A thread for reading data from the arduino.
 *
 */
public class Thread_ReadData extends Thread implements SerialPortEventListener {



    private FileOutputStream stream;
    private WindowDataReadArduino data;
    private ArduinoSerialPort port;
    private boolean done = false;
    private boolean got_file_size = false;
    private String temp = "";


    /**
     * Public constructor
     * @param port; An open serial port
     * @param data; Window data class
     * @throws SerialPortException
     */
    public Thread_ReadData(ArduinoSerialPort port, WindowDataReadArduino data) throws SerialPortException {
        if(!port.isOpened()){
            throw new SerialPortException(port.getPortName(), "Thread read data constructor", SerialPortException.TYPE_PORT_NOT_OPENED);

        }
        this.data = data;
        this.port = port;
    }

    @Override
    public void run() {
        super.run();
        Calendar start = Calendar.getInstance();
        try {
            File f = IoTmodel.getInstance().getDataDir();
            File csv = new File(f, "data.csv");
            boolean fileExist = csv.exists();
            // rename old file to prevent data loss
            if(fileExist){
                data.appendLogData("Hernoemen van oud bestand");
                if(!csv.renameTo(new File(f, String.format("data-%1d.csv", System.currentTimeMillis())))){
                    data.appendLogData("Cannot rename data.csv");
                    return;
                }
            }
            // open file
            data.appendLogData("Bestand aanmaken");
            stream = new FileOutputStream(csv);
            data.resetProgress();
            // request data from arduino
            data.appendLogData("Data opvragen van klimaatscanner");
            port.addEventListener(this);
            port.writeString(ArduinoSerialPort.MESSAGE_READ_DATA);

            // wait for done
            while(!done){
                sleep(1000);
            }
            Calendar end = Calendar.getInstance();

            long diff = end.get(Calendar.MILLISECOND) - start.get(Calendar.MILLISECOND);
            end.setTimeInMillis(diff);
            data.appendLogData(String.format("Tijd nodig voor het uitlezen: %d:%d (m:s)",end.get(Calendar.MINUTE),end.get(Calendar.SECOND)));
            // close
            data.appendLogData("Sluiten bestaden");
            stream.close();
            port.removeEventListener();
            Desktop.getDesktop().open(f);

        } catch (Exception exception) {
            data.appendLogData("Onverwachte fout, zie error bericht voor meer details.");
            data.appendLogData(exception.getMessage());
        }

    }



    @Override
    public synchronized void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                // read bytes from serial
                byte[] buff = port.readBytes(event.getEventValue());
                // check if we need to add header information to the output file
                String message = new String(buff);
                if(!got_file_size){
                    message = getFileSize(message);
                    buff = message.getBytes();
                    if(message.isEmpty()){
                        return;
                    }
                }

                // check for end of file command
                if(message.endsWith(ArduinoSerialPort.ANSWER_READ_DATA)){
                    done = true;
                    String trim = message.substring(0,message.length() - ArduinoSerialPort.ANSWER_READ_DATA.length());
                    stream.write(trim.getBytes());
                    System.out.println("einde file");
                    data.appendLogData("Einde csv file bereikt");
                    return;
                }

                // some progress has been made. Update progressbar
                data.addProgress(buff.length);

                // write data to file
                stream.write(buff);
            } catch (SerialPortException ex) {
                data.appendLogData("Uitlezen mislukt, verwijder data.csv en probeer opnieuw");
                done = true;// something went wrong stop this thread.
            } catch (IOException e) {
                e.printStackTrace();
                done = true;
            }
        }
    }

    private synchronized String getFileSize(String message) {
        temp += message;
        if(!temp.contains("\n")){
            return ""; // do nothing
        }
        got_file_size = true;
        String data = temp.substring(temp.indexOf("\n")+1);
        String fileSize = temp.substring(0, temp.indexOf("\n"));
        this.data.setFileSize(Integer.parseInt(fileSize.trim()));

        return data;
    }

}
