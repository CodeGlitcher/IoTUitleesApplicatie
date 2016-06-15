package iot.meetding.view.beans;


import javax.swing.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Rob on 29-5-2016.
 *
 */
public class ConfigQuestion extends Observable {

    public static final int ROWS_QUESTION = 4;
    public static final int ROWS_ANSWER =3;

    private static int questionID = 0;
    private String[] question;
    private Boolean deleted;
    private ArrayList<String[]> answers;
    private int key;

    public ConfigQuestion(){
        key = questionID;
        questionID++;

        question = new String[ROWS_QUESTION];
        for(int i = 0; i<question.length; i++){
            question[i] = "";
        }
        answers = new ArrayList<>();

        deleted = false;
    }



    public void setQuestion(int partNr, String part){
        question[partNr] = part;
        setChanged();
        notifyObservers();
    }

    public String getKey(){
        return "#" + key;
    }

    public String getQuestion(){
        return String.format("%s %s %s %s", question[0].trim(), question[1].trim(), question[2].trim(), question[3].trim());
    }

    public String[] getQuestionParts(){
        return question;
    }
    public String getQeustionPart(int part){
        return question[part];
    }

    public void delete(){
        deleted = true;
        setChanged();
        notifyObservers();
    }

    public Boolean isDeleted(){
        return deleted;
    }


    public ArrayList<String[]> getAnswers() {

        return answers;
    }

    public void setAnswers(ArrayList<String[]> answers){
        this.answers = answers;
        setChanged();
        notifyObservers();
    }
}
