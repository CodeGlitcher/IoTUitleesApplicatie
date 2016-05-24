package iot.meetding;

import iot.meetding.model.IoTmodel;
import iot.meetding.view.MainWindow;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        // Open main window
        MainWindow main = new MainWindow();
        main.setVisible(true);
        // on shutdown close all com ports
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                IoTmodel.getInstance().removeAllPorts();
            }
        }));
    }
}

