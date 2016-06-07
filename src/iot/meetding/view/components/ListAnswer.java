package iot.meetding.view.components;

import iot.meetding.view.beans.ConfigQuestion;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rob on 6-6-2016.
 */
public class ListAnswer implements Observer {
    private JPanel panel1;
    private HintTextField textField_AnswerPart1;
    private HintTextField textField_AnswerPart2;
    private JButton button_RemoveAnswer;
    private JLabel label_Chars1;
    private JLabel label_Answer2;

    public ListAnswer(ConfigQuestion question) {

    }


    private void createUIComponents() {
        textField_AnswerPart1 = new HintTextField("");
        textField_AnswerPart2 = new HintTextField("");
    }

    @Override
    public void update(Observable o, Object arg) {

    }

}
