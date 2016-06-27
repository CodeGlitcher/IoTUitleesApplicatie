package iot.meetding.threads;

import com.sun.org.apache.bcel.internal.generic.DSTORE;
import iot.meetding.ArduinoSerialPort;
import iot.meetding.model.IoTmodel;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static jssc.SerialPortException.TYPE_PORT_NOT_OPENED;

/**
 * Created by Rob on 15-6-2016.
 *
 */
public class Thread_SendConfig extends Thread{


    private JDialog dlg;
    private JProgressBar  dpb;
    private ArrayList<byte[]> bytes;
    private ArduinoSerialPort port;
    public Thread_SendConfig(ArduinoSerialPort port) throws SerialPortException {
        this.port = port;
        if(!port.isOpened()) {
            throw new SerialPortException(port.getPortName(),"port not open", TYPE_PORT_NOT_OPENED);
        }
        IoTmodel model =  IoTmodel.getInstance();

        dlg = new JDialog(model.getFrame(), "Even geduld a.u.b.", true);
        dpb = new JProgressBar(0,100);

        dlg.add(BorderLayout.CENTER, dpb);
        dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlg.setSize(300, 75);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (screenSize.width - dlg.getWidth()) / 2;
        final int y = (screenSize.height - dlg.getHeight()) / 2;
        dlg.setLocation(x, y);

    }


    @Override
    public void run(){
        super.run();

        Thread t = new Thread(() -> {
            dlg.setVisible(true);
        });
        t.start();
        try {
            // create the config file
            bytes =IoTmodel.getInstance().createConfigFile();
            dpb.setMaximum(bytes.size());
            System.out.println(bytes.size() + 5);
            // tell the arduino we are sending a new config file
            port.writeString(ArduinoSerialPort.MESSAGE_SEND_CONFIG);

            // the arduino has a small serial buffer (66 byte)
            // we cannot send the full byte array so we send a single byte and then wait 1ms.
            // this gives the arduino to proses the data
            for (byte[] buff : bytes) {
                for(byte b : buff){
                    port.writeByte(b);
                    sleep(1);

                }
                dpb.setValue(dpb.getValue() + 1);
            }

            sleep(100);
            // send end of file
            port.writeString(ArduinoSerialPort.ANSWER_SEND_CONFIG);
            dpb.setValue(dpb.getValue() + 1);

            // give arduino time to finish
            sleep(5000);

            // Send the current time to the arduino to ajuist the rtc clock
            port.writeString(ArduinoSerialPort.MESSAGE_SEND_TIME);
            dpb.setValue(dpb.getValue() + 1);

            String time = getTime();

            port.writeBytes(time.getBytes());

            dpb.setValue(dpb.getValue() + 1);
            sleep(100);
            dpb.setValue(dpb.getValue() + 1);
            port.writeString(ArduinoSerialPort.ANSWER_SEND_TIME);
            dpb.setValue(dpb.getValue() + 1);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error sending");
        } finally {
            dlg.setVisible(false);
        }
    }

    private String getTime(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Amsterdam"));
        long time =  cal.getTimeInMillis(); // this returns the UTC time
        time += cal.get(Calendar.DST_OFFSET); // add daylight saveing time offset
        time += cal.getTimeZone().getRawOffset(); // add time zone offset
        return (time / 1000) + ""; // convert ms to s;

    }
}
