package iot.meetding.view;

import iot.meetding.controller.verifiers.IntVerify;
import iot.meetding.view.components.HintTextField;

import javax.swing.*;

/**
 * Created by Rob on 31-5-2016.
 *
 */
public class ConfigurationPanel extends JPanel {
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
    private JList list_questions;
    private JButton vraagToevoegenButton;

    private void createUIComponents() {
        // TODO: place custom component creation code here
        textField_intervalTime = new HintTextField("Hoe vaak moet er gemeten worden? Laat leeg om geen intervalmetingen te donen.");
        textField_intervalQuestion = new HintTextField("Hoeveel tijd moet er tussen bevragingen zitten");
        textField_startTime = new HintTextField("Starttijd bevragingen en intervalmetingen (per uur)", new IntVerify());
        textField_endTime = new HintTextField("Eindtijd bevragingen en intervalmetingen (per uur)", new IntVerify());

    }
}
