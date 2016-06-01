package iot.meetding.view.beans;

import java.util.Observable;

/**
 * Created by Rob on 29-5-2016.
 *
 */
public abstract class ConfigItem extends Observable {

    public enum Type {question, integer }
    protected int key;
    private Type type;

    public ConfigItem(Type t, int key){
        this.type = t;
        this.key = key;
    }
    public void changed(){
        setChanged();
        notifyObservers();
    }
    public void changed(Object arg){
        setChanged();
        notifyObservers(arg);
    }

}
