package iot.meetding;

import iot.meetding.model.IoTmodel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rob on 18-5-2016.
 * A simple log class for output through console
 */
public class Logger {

    public static boolean enable = true;

    public static void log(String message){
        if(!enable){
            return;
        }
        System.out.print(getCurrentTimeStamp());
        System.out.print(":");
        System.out.println(message);

    }


    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }
}

