package iot.meetding;



import jssc.SerialPort;
import jssc.SerialPortList;

import java.util.Enumeration;

public class Main {

    public static void main(String[] args) {
      //  MainWindow main = new MainWindow();
    //    main.setVisible(true);
//        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
//
//        //First, Find an instance of serial port as set in PORT_NAMES.
//        while (portEnum.hasMoreElements()) {
//            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
//            System.out.println(currPortId.getName());
//        }

        String[] portNames = SerialPortList.getPortNames();
        for (String portName : portNames) {
            System.out.println(portName);
            SerialPort p = new SerialPort(portName);
            System.out.println(p.getPortName());
        }

    }



}
