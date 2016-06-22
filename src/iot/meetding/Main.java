package iot.meetding;

import iot.meetding.model.IoTmodel;
import iot.meetding.view.MainWindow;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Main {

    /**
     * Start program
     *
     * @param args, args for startup
     */
    public static void main(String[] args) {
//        // Open main window
        MainWindow main = new MainWindow();
        main.setVisible(true);
        // set frame, used to open dialogs
        IoTmodel.getInstance().setFrame(main);
        // on shutdown close all com ports
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            IoTmodel.getInstance().removeAllPorts();
        }));



    }


    public static int elapsed(Calendar before, Calendar after, int field) {
        Calendar clone = (Calendar) before.clone(); // Otherwise changes are been reflected.
        int elapsed = -1;
        while (!clone.after(after)) {
            clone.add(field, 1);
            elapsed++;
        }
        return elapsed;
    }
}

