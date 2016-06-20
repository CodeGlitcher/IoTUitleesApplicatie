package iot.meetding;

import iot.meetding.model.IoTmodel;
import iot.meetding.view.MainWindow;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Main {

    public static void main(String[] args) {
        // Open main window




        MainWindow main = new MainWindow();
        main.setVisible(true);
        IoTmodel.getInstance().setFrame(main);
        // on shutdown close all com ports
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                IoTmodel.getInstance().removeAllPorts();
            }
        }));







//Set extension filter

//Show open file dialog


    }
}

