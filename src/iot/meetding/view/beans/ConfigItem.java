package iot.meetding.view.beans;

/**
 * Created by Rob on 29-5-2016.
 */
public abstract class ConfigItem {

    public enum Type {question, integer }
    protected String key;
    private Type type;

    public ConfigItem(Type t, String key){
        this.type = t;
        this.key = key;
    }

}
