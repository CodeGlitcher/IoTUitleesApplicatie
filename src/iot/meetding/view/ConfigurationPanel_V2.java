package iot.meetding.view;

import iot.meetding.controller.ButtonColumn;
import iot.meetding.controller.verifiers.IntVerify;
import iot.meetding.model.IoTmodel;
import iot.meetding.view.beans.ConfigItem;
import iot.meetding.view.beans.ConfigQuestion;
import iot.meetding.view.components.HintTextField;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rob on 8-6-2016.
 */
public class ConfigurationPanel_V2 implements Observer, ActionListener {

    private JPanel panel_MainConfig;
    private HintTextField textField_IntervalTime;
    private HintTextField textField_intervalQuestion;
    private HintTextField textField_StartTime;
    private HintTextField textField_EndTime;
    private JCheckBox checkbox_ma;
    private JButton button_DownloadFromArduino;
    private JButton button_LoadLocalFile;
    private JButton button_SendArduino;
    private JButton button_Save;
    private JTable table_Qeustion;
    private JCheckBox checkbox_wo;
    private JCheckBox checkbox_di;
    private JCheckBox checkbox_do;
    private JCheckBox checkbox_vr;
    private JCheckBox checkbox_za;
    private JCheckBox checkbox_zo;
    private JScrollPane scroll;
    private DefaultTableModel tModel;


    public void createUIComponents() {
        textField_IntervalTime = new HintTextField("Hoe vaak moet er gemeten worden? Vul 0 in om geen intervalmetingen te donen.", new IntVerify());
        textField_intervalQuestion = new HintTextField("Hoeveel tijd moet er tussen bevragingen zitten", new IntVerify());
        textField_StartTime = new HintTextField("Starttijd bevragingen en intervalmetingen (per uur)", new IntVerify());
        textField_EndTime = new HintTextField("Eindtijd bevragingen en intervalmetingen (per uur)", new IntVerify());
        IoTmodel.getInstance().addObserver(this);

    }


    public ConfigurationPanel_V2() {
        tModel = new QuestionTableModel();
        table_Qeustion.setModel(tModel);
        tModel.addColumn("ID");
        tModel.addColumn("Vraag");
        tModel.addColumn("Bewerk");
        tModel.addColumn("Verwijder");
        table_Qeustion.setVisible(true);
        table_Qeustion.setFillsViewportHeight(true);

        Action edit = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int modelRow = Integer.valueOf(e.getActionCommand());
                IoTmodel m = IoTmodel.getInstance();
                EditQuestion dialog = new EditQuestion(m.getQuestions().get(modelRow));
                dialog.pack();
                dialog.setVisible(true);
            }
        };

        Action delete = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int modelRow = Integer.valueOf(e.getActionCommand());
                IoTmodel.getInstance().getQuestions().get(modelRow).delete();
            }
        };
        table_Qeustion.getColumnModel().getColumn(0).setMaxWidth(50);
        table_Qeustion.getColumnModel().getColumn(1).setPreferredWidth(400);
        table_Qeustion.getColumnModel().getColumn(2).setMaxWidth(80);
        table_Qeustion.getColumnModel().getColumn(3).setMaxWidth(80);

        new ButtonColumn(table_Qeustion, edit, 2);
        new ButtonColumn(table_Qeustion, delete, 3);

        button_Save.addActionListener(this);
        fillUI();
    }

    @Override
    public void update(Observable o, Object arg) {
        fillUI();
    }


    private void fillUI() {
        tModel.setRowCount(0);
        IoTmodel model = IoTmodel.getInstance();

        for (ConfigQuestion q : model.getQuestions()) {
            tModel.addRow(createRow(q));
        }
        textField_EndTime.setText(model.getEndConfig().getValue() + "");
        textField_StartTime.setText(model.getStartConfig().getValue() + "");
        textField_intervalQuestion.setText(model.getQuestionConfig().getValue() + "");
        textField_IntervalTime.setText(model.getMeasurementsConfig().getValue() + "");
        for (ConfigItem<Boolean> day : model.getDayConfig()) {
            switch (day.getKey()) {
                case "ma":
                    checkbox_ma.setSelected(day.getValue());
                case "di":
                    checkbox_di.setSelected(day.getValue());
                case "wo":
                    checkbox_wo.setSelected(day.getValue());
                case "do":
                    checkbox_do.setSelected(day.getValue());
                case "vr":
                    checkbox_vr.setSelected(day.getValue());
                case "za":
                    checkbox_za.setSelected(day.getValue());
                case "zo":
                    checkbox_zo.setSelected(day.getValue());
                default:
                    break;

            }
        }
    }

    private Object[] createRow(ConfigQuestion q) {
        Object[] result = new Object[4];

        result[0] = q.getKey();
        result[1] = q.getQuestion();
        result[2] = new ImageIcon(getClass().getResource("/resource/config/pencil.png"));
        result[3] = new ImageIcon(getClass().getResource("/resource/config/close.png"));
        return result;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        IoTmodel.getInstance().createQuestion();
        updateModel();
    }


    private void updateModel() {
        IoTmodel m = IoTmodel.getInstance();
        m.getMeasurementsConfig().setValue((Integer.parseInt(textField_IntervalTime.getText())));
        m.getQuestionConfig().setValue((Integer.parseInt(textField_intervalQuestion.getText())));
        m.getStartConfig().setValue((Integer.parseInt(textField_StartTime.getText())));
        m.getEndConfig().setValue((Integer.parseInt(textField_EndTime.getText())));


        for (ConfigItem<Boolean> day : m.getDayConfig()) {
            switch (day.getKey()) {
                case "ma":
                    day.setValue(checkbox_ma.isSelected());
                case "di":
                    day.setValue(checkbox_di.isSelected());
                case "wo":
                    day.setValue(checkbox_wo.isSelected());
                case "do":
                    day.setValue(checkbox_do.isSelected());
                case "vr":
                    day.setValue(checkbox_vr.isSelected());
                case "za":
                    day.setValue(checkbox_za.isSelected());
                case "zo":
                    day.setValue(checkbox_zo.isSelected());
                default:
                    break;

            }
        }
    }

    private class QuestionTableModel extends DefaultTableModel{
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 2 || column ==3;
        }
    }
}