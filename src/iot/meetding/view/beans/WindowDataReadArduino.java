package iot.meetding.view.beans;

import java.util.Observable;

/**
 * A holder for data for Data reading window
 */
public class WindowDataReadArduino extends Observable {
    private String logData; // log for user to see data
    private int fileSize;
    private int progress = 0;
    public WindowDataReadArduino() {
        logData = "";
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

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
        change();
    }
    public int getFileSize(){
        return this.fileSize;
    }
    public void addProgress(int progress){
        this.progress += progress;
        change();
    }
    public int getProgress(){
        return this.progress;
    }
    public void resetProgress(){
        this.progress = 0;
        change();
    }
}