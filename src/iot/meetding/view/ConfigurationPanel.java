package iot.meetding.view;

import iot.meetding.view.components.HintTextField;

import javax.swing.*;

/**
 * Created by Rob on 31-5-2016.
 *
 */
public class ConfigurationPanel extends JPanel {
    private HintTextField hintTextField1;
    private HintTextField hintTextField2;
    private HintTextField hintTextField3;
    private HintTextField hintTextField4;

    private JPanel p;
    private JPanel test;

    private void createUIComponents() {
        // TODO: place custom component creation code here
        hintTextField1 = new HintTextField("TEST!!");
        hintTextField2 = new HintTextField("TEST1!!");
        hintTextField3 = new HintTextField("TEST2!!");
        hintTextField4 = new HintTextField("TEST3!!");

    }
}
