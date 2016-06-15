package iot.meetding.Threads;

import iot.meetding.ArduinoSerialPort;
import iot.meetding.model.IoTmodel;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Rob on 15-6-2016.
 */
public class Thread_SendConfig extends Thread implements SerialPortEventListener {


    private JDialog dlg;
    private JProgressBar  dpb;
    private ArrayList<byte[]> bytes;
    private ArduinoSerialPort port;
    public Thread_SendConfig(ArduinoSerialPort port) {
        this.port = port;
        IoTmodel model =  IoTmodel.getInstance();
        bytes =model.createConfigFile();




        dlg = new JDialog(model.getFrame(), "Even gedult a.u.b.", true);
        dpb = new JProgressBar(0,100);
        dpb.setMaximum(bytes.size());
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
    public void run(){
        super.run();
        try {
            System.out.println(bytes.size());
            port.writeString(ArduinoSerialPort.MESSAGE_SEND_CONFIG);

            for (byte[] buff : bytes) {
                System.out.println(buff.length);
                for(byte b : buff){

                    port.writeBytes(buff);
                    sleep(1);

                }
                dpb.setValue(dpb.getValue() + 1);
            }

            sleep(100);
            port.writeString(ArduinoSerialPort.ANSWER_SEND_CONFIG);

            sleep(5000);
            port.writeString(ArduinoSerialPort.MESSAGE_SEND_TIME);
            String time = System.currentTimeMillis() / 1000 + "";
            port.writeBytes(time.getBytes());
            sleep(100);

            port.writeString(ArduinoSerialPort.ANSWER_SEND_TIME);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error sending");
        } finally {
            dlg.setVisible(false);
        }
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {

    }
}
