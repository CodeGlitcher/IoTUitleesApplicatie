package iot.meetding.view.components;

import iot.meetding.view.beans.ConfigQuestion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rob on 29-5-2016.
 */
public class ListQuestion implements ActionListener, Observer {
    private JButton button_change;
    private JLabel key;
    private JButton button_remove;
    private JPanel panel;
    private JLabel textField_question;
    private JTextField textField1;


    private ConfigQuestion question;

    public ListQuestion(ConfigQuestion question) {
        this.question = question;
        question.addObserver(this);

        key.setText(question.getKey());
        textField_question.setText(question.getQuestion());
    }

    public JPanel getPanel() {
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void update(Observable o, Object arg) {
        textField_question.setText(question.getQuestion());
    }

}
