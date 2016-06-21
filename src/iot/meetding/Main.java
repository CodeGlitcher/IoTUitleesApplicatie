package iot.meetding;

import iot.meetding.model.IoTmodel;
import iot.meetding.view.MainWindow;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class Main {

    /**
     * Start program
     * @param args, args for startup
     */
    public static void main(String[] args) {
        // Open main window
        MainWindow main = new MainWindow();
        main.setVisible(true);
        IoTmodel.getInstance().setFrame(main);
        // on shutdown close all com ports
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            IoTmodel.getInstance().removeAllPorts();
        }));


    }
}

