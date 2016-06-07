package iot.meetding.view.beans;

import java.util.ArrayList;

/**
 * Created by Rob on 29-5-2016.
 *
 */
public class ConfigQuestion extends ConfigItem {

    private static int questionID = 0;
    private String[] question;
    private Boolean deleted;
    private ArrayList<String[]> answers;

    public ConfigQuestion(){
        super(Type.question, questionID);
        questionID++;
        question = new String[5];
        answers = new ArrayList<>();
        deleted = false;
    }

    public String getQuestion(){
        return String.format("%s\n%s\n%s\n%s\n%s", question[0], question[1], question[2], question[3], question[4]);
    }

    public String getQeustionPart(int part){
        return question[part];
    }

    public void delete(){
        deleted = true;
    }

    public Boolean isDeleted(){
        return deleted;
    }












}
