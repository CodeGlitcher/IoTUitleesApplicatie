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
 *
 */
public class Thread_ReadData extends Thread implements SerialPortEventListener {


    private FileOutputStream stream;
    private WindowDataReadArduino data;
    private ArduinoSerialPort port;
    private boolean done = false;
    private boolean isFirstLine = true;
    private boolean got_file_size = false;
    private boolean fileExist;
    private String temp = "";
    public Thread_ReadData(ArduinoSerialPort port, WindowDataReadArduino data) throws SerialPortException {
        if(!port.isOpened()){
            throw new SerialPortException(port.getPortName(), "Thread read data constructor", SerialPortException.TYPE_PORT_NOT_OPENED);

        }
        this.data = data;
        this.port = port;
    }

    private Calendar start;

    @Override
    public void run() {
        super.run();
        start = Calendar.getInstance();
        try {
            File f = IoTmodel.getInstance().getDataDir();
            File csv = new File(f, "data.csv");
            fileExist = csv.exists();
            // rename old file to prevent data loss
            if(fileExist && !data.appendCSV()){
                data.appendLogData("Renaming old file");
                if(!csv.renameTo(new File(f, String.format("data-%1d.csv", System.currentTimeMillis())))){
                    data.appendLogData("Cannot rename data.csv");
                    fileExist = false;
                    return;
                }
            }
            // open file
            data.appendLogData("Opening file");
            stream = new FileOutputStream(csv, data.appendCSV());
            data.resetProgress();
            // request data from arduino
            data.appendLogData("Requesting data from arduino");
            port.addEventListener(this);
            port.writeString(ArduinoSerialPort.MESSAGE_READ_DATA);

            // file for done
            while(!done){
                sleep(1000);
            }
            Calendar end = Calendar.getInstance();

            long diff = end.get(Calendar.MILLISECOND) - start.get(Calendar.MILLISECOND);
            end.setTimeInMillis(diff);
            data.appendLogData("Total time needed: M:s" + end.get(Calendar.MINUTE) + ":" + end.get(Calendar.SECOND));
            // close
            data.appendLogData("Closing file");
            stream.close();
            port.removeEventListener();
            Desktop.getDesktop().open(f);

        } catch (Exception exception) {
            data.appendLogData("Error reading");
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
                data.addProgress(buff.length);
                // if we are stil on the isFirstLine line
                if(data.appendCSV() && isFirstLine && fileExist){
                    if(message.contains("\n")){ // ignore data until \n is found
                        String tmp = message.substring(message.indexOf("\n")+1, message.length()); // get data after \n en write it.
                        stream.write(tmp.getBytes());
                        isFirstLine = false;
                    }
                    return;
                }
                // check for end of file command
                if(message.endsWith(ArduinoSerialPort.ANSWER_READ_DATA)){
                    done = true;
                    String trim = message.substring(0,message.length() - ArduinoSerialPort.ANSWER_READ_DATA.length());
                    stream.write(trim.getBytes());
                    data.appendLogData("End of File command received");
                    return;
                }

                // write data to file
                stream.write(buff);
            } catch (SerialPortException ex) {
                data.appendLogData("Reading failed, data corrupt. Delete data file and try aggain");
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
