package iot.meetding.view.beans;

import java.util.Observable;

/**
 * A holder for data for Data reading window
 */
public class WindowDataReadArduino extends Observable {
    private String logData;

    private boolean appendCSV;
    public WindowDataReadArduino() {
        logData = "";
        appendCSV = false;
    }

    public String getLogData() {
        return logData;
    }

    public void appendLogData(final String logData) {
        this.logData = this.logData.concat(logData + "\n");
        change();
    }

    public void clearLogData(){
        this.logData = "";
        change();
    }

    private void change(){
        setChanged();
        notifyObservers();
    }

    public boolean appendCSV() {
        return appendCSV;
    }

    public void setAppendCSV(boolean append){
        this.appendCSV = append;
        change();
    }
}