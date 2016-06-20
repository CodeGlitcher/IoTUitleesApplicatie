package iot.meetding.view.beans;

import java.util.Observable;

/**
 * Created by Rob on 29-5-2016.
 * A class to hold config information.
 */
public class ConfigItem<x> extends Observable {

    private String key;
    private x value;

    public ConfigItem(String key, x value){
        this.key = key;
        this.value = value;
    }

    private void changed(){
        setChanged();
        notifyObservers();
    }
    public void changed(Object arg){
        setChanged();
        notifyObservers(arg);
    }

    public x getValue(){
        return value;
    }
    public void setValue(x value) {
        this.value = value;
        changed();
    }

    public String getKey(){
        return key;
    }


}
