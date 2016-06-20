package iot.meetding.threads;

import iot.meetding.ArduinoSerialPort;
import iot.meetding.model.IoTmodel;
import iot.meetding.view.beans.WindowDataReadArduino;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Rob on 17-6-2016.
 *
 */
public class Thread_ReadConfig extends Thread implements SerialPortEventListener {



    private JDialog dlg;
    private JProgressBar  dpb;
    private FileOutputStream stream;
    private ArduinoSerialPort port;
    private boolean done = false;
    public Thread_ReadConfig(ArduinoSerialPort port) throws SerialPortException {
        if(!port.isOpened()){
            throw new SerialPortException(port.getPortName(), "Thread read data constructor", SerialPortException.TYPE_PORT_NOT_OPENED);
        }
        this.port = port;


        IoTmodel model = IoTmodel.getInstance();
        dlg = new JDialog(model.getFrame(), "Even gedult a.u.b.", true);
        dpb = new JProgressBar(0,100);

        dlg.add(BorderLayout.CENTER, dpb);
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.setSize(300, 75);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (screenSize.width - dlg.getWidth()) / 2;
        final int y = (screenSize.height - dlg.getHeight()) / 2;
        dlg.setLocation(x, y);

        Thread t = new Thread(() -> {
            dlg.setVisible(true);
        });
        t.start();
    }





    @Override
    public void run() {
        super.run();
        IoTmodel model = IoTmodel.getInstance();
        File configDir;
        try {
            dpb.setMaximum(3);
            dpb.setValue(0);
            configDir = model.getConfigDir();

            File tempConfig = new File(configDir, "tempConfig.ini");
            stream = new FileOutputStream(tempConfig,false);
            port.addEventListener(this);
            port.writeString(ArduinoSerialPort.MESSAGE_READ_CONFIG);

            dpb.setValue(dpb.getValue() + 1);
            while (!done) {
                sleep(1000);

            }
            stream.flush();
            stream.close();
            port.removeEventListener();

            dpb.setValue(dpb.getValue() + 1);
            model.readConfig(tempConfig);

            dpb.setValue(dpb.getValue() + 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dlg.setVisible(false);
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

                if(message.endsWith(ArduinoSerialPort.ANSWER_READ_CONFIG)){
                    String trim = message.substring(0,message.length() - ArduinoSerialPort.ANSWER_READ_CONFIG.length());
                    stream.write(trim.getBytes());
                    done = true;
                    return;
                }
                // write data to file
                stream.write(buff);
            } catch (SerialPortException ex) {

                done = true;// something went wrong stop this thread.
            } catch (IOException e) {
                e.printStackTrace();
                done = true;
            }
        }
    }
}
