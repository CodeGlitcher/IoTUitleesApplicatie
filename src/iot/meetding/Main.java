package iot.meetding;

import iot.meetding.view.MainWindow;
import iot.meetding.view.SerialOut;
import jssc.SerialPort;
import jssc.SerialPortList;

import java.security.cert.Extension;
import java.util.Enumeration;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        MainWindow main = new MainWindow();
        main.setVisible(true);
        Random r = new Random(500);
        while (true) {
            SerialOut t = new SerialOut();
            String s = r.nextInt() + "";
            t.setText(s);
            main.setData(t);
            try {
                Thread.sleep(500);
            } catch (
                Exception z
                ) {

            }
        }
    }
}
