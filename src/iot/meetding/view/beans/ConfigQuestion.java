package iot.meetding.view.beans;

import java.util.ArrayList;

/**
 * Created by Rob on 29-5-2016.
 */
public class ConfigQuestion extends ConfigItem {

    private static int questionID = 0;
    private String[] question = new String[4];
    private ArrayList<String[]> answers;

    public ConfigQuestion(){
        super(Type.question, "vraag"+questionID);
        questionID++;
        answers = new ArrayList<>();
    }




}
