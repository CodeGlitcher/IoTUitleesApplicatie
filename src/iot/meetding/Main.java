package iot.meetding;

import iot.meetding.model.IoTmodel;
import iot.meetding.view.MainWindow;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        // Open main window
            try {
            File dir = IoTmodel.getInstance().getConfigDir();
            IoTmodel.getInstance().readConfig(new File(dir, "local_config.ini"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainWindow main = new MainWindow();
        main.setVisible(true);
        // on shutdown close all com ports
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                IoTmodel.getInstance().removeAllPorts();
            }
        }));

       //IoTmodel.getInstance().createConfigFile();



//Set extension filter

//Show open file dialog


    }
}

