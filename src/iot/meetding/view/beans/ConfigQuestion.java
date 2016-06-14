package iot.meetding.view.beans;


import javax.swing.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Rob on 29-5-2016.
 *
 */
public class ConfigQuestion extends Observable {

    private static int questionID = 0;
    private String[] question;
    private Boolean deleted;
    private ArrayList<String[]> answers;
    private int key;

    public ConfigQuestion(){
        key = questionID;
        questionID++;

        question = new String[4];
        question[0] = "Dit is een";
        question[1] = "voor-";
        question[2] = "beeld";
        question[3] = ".";
        answers = new ArrayList<>();

        String[] answer = new String[3];
        answer[0] = "1";
        answer[1] = "2";
        answer[2] = "3";
        answers.add(answer);
        deleted = false;
    }



    public void setQuestion(int partNr, String part){
        question[partNr] = part;
        setChanged();
        notifyObservers();
    }

    public String getKey(){
        return "#: " + key;
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
