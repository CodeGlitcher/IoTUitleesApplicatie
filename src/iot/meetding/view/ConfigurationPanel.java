package iot.meetding.view;

import iot.meetding.controller.ListConfigRender;
import iot.meetding.controller.verifiers.IntVerify;
import iot.meetding.model.IoTmodel;
import iot.meetding.view.beans.ConfigItem;
import iot.meetding.view.beans.ConfigQuestion;
import iot.meetding.view.components.HintTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Rob on 31-5-2016.
 *
 */
public class ConfigurationPanel extends JPanel implements ActionListener {
    private HintTextField textField_intervalTime;
    private HintTextField textField_intervalQuestion;
    private HintTextField textField_endTime;
    private HintTextField textField_startTime;

    private JPanel p;
    private JPanel ConfigMain;
    private JButton button_downloadArduino;
    private JButton button_sendArduino;
    private JButton button_loadLocalConfig;
    private JCheckBox checkbox_monday;
    private JCheckBox checkbox_tuesday;
    private JCheckBox checkbox_wednesday;
    private JCheckBox checkbox_friday;
    private JCheckBox checkbox_thursday;
    private JCheckBox checkbox_saturday;
    private JCheckBox checkbox_sunday;
    private JList<ConfigQuestion> list_questions;
    private JButton button_AddQuestion;
    private JButton button_Save;

    private IoTmodel model;

    public ConfigurationPanel(){
        model = IoTmodel.getInstance();
        button_downloadArduino.addActionListener(this);
        button_sendArduino.addActionListener(this);
        button_loadLocalConfig.addActionListener(this);
        button_Save.addActionListener(this);
        button_AddQuestion.addActionListener(this);

        DefaultListModel<ConfigQuestion> model = new DefaultListModel<>();

        for(ConfigQuestion q : this.model.getQuestions())
        {
            model.addElement(q);
        }
        list_questions.setModel(model);
        list_questions.setCellRenderer(new ListConfigRender());


        fillUI();
    }



    private void createUIComponents() {
        // TODO: place custom component creation code here
        textField_intervalTime = new HintTextField("Hoe vaak moet er gemeten worden? Laat leeg om geen intervalmetingen te donen.");
        textField_intervalQuestion = new HintTextField("Hoeveel tijd moet er tussen bevragingen zitten");
        textField_startTime = new HintTextField("Starttijd bevragingen en intervalmetingen (per uur)", new IntVerify());
        textField_endTime = new HintTextField("Eindtijd bevragingen en intervalmetingen (per uur)", new IntVerify());

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if(action.equals(button_downloadArduino.getActionCommand())){
            return;
        }
        if(action.equals(button_Save.getActionCommand())){
            return;
        }
        if(action.equals(button_loadLocalConfig.getActionCommand())){
            return;
        }
        if(action.equals(button_AddQuestion.getActionCommand())){
            return;
        }
        if(action.equals(button_sendArduino.getActionCommand())){
            return;
        }
    }


    private void fillUI(){
        textField_endTime.setText(model.getEndConfig().getValue() + "");
        textField_startTime.setText(model.getStartConfig().getValue() + "");
        textField_intervalQuestion.setText(model.getQuestionConfig().getValue() + "");
        textField_intervalTime.setText(model.getMeasurementsConfig().getValue() + "");

        for (ConfigItem<Boolean> day: model.getDayConfig()) {
            switch (day.getKey()){
                case "ma": checkbox_monday.setSelected(day.getValue());
                case "di": checkbox_thursday.setSelected(day.getValue());
                case "wo": checkbox_wednesday.setSelected(day.getValue());
                case "do": checkbox_tuesday.setSelected(day.getValue());
                case "vr": checkbox_friday.setSelected(day.getValue());
                case "za": checkbox_saturday.setSelected(day.getValue());
                case "zo": checkbox_sunday.setSelected(day.getValue());

            }
        }
        list_questions.updateUI();
    }



}
