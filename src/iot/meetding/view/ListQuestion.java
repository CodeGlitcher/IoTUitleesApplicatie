package iot.meetding.view;

import iot.meetding.view.beans.ConfigQuestion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rob on 29-5-2016.
 *
 */
public class ListQuestion implements ActionListener, Observer {
    private JButton bewerkButton;
    private JLabel key;
    private JButton button1;
    private JPanel panel;


    private ConfigQuestion question;

    public ListQuestion(ConfigQuestion question) {
        this.question = question;
        question.addObserver(this);



    }
    public JPanel getPanel(){
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
