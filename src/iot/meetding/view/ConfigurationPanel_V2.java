package iot.meetding.view;

import iot.meetding.threads.Thread_ReadConfig;
import iot.meetding.threads.Thread_SendConfig;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rob on 8-6-2016.
 * Class that holds logic for configuration panel
 * (V2 because first configuration panel files went corrupt)
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
    private JButton button_AddQuestion;
    private DefaultTableModel tModel;


    private int COLUMN_ID = 0;
    private int COLUMN_QUESTION =1;
    private int COLUMN_EDIT = 2;
    private int COLUMN_DELETE =3;

    /**
     * Create custom components
     */
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

        configureTable();

        button_Save.addActionListener(this);
        button_AddQuestion.addActionListener(this);
        button_DownloadFromArduino.addActionListener(this);
        button_SendArduino.addActionListener(this);
        button_LoadLocalFile.addActionListener(this);

        fillUI();
    }

    /**
     * Build table and add column button
     */
    private void configureTable() {
        // create actions for buttons
        Action edit = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int modelRow = Integer.valueOf(e.getActionCommand());
                IoTmodel m = IoTmodel.getInstance();
                EditQuestion dialog = new EditQuestion(null,true, m.getQuestions().get(modelRow));
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
        // set column sizes
        table_Qeustion.getColumnModel().getColumn(COLUMN_ID).setMaxWidth(50);
        table_Qeustion.getColumnModel().getColumn(COLUMN_QUESTION).setPreferredWidth(400);
        table_Qeustion.getColumnModel().getColumn(COLUMN_EDIT).setMaxWidth(80);
        table_Qeustion.getColumnModel().getColumn(COLUMN_DELETE).setMaxWidth(80);
        table_Qeustion.getTableHeader().setReorderingAllowed(false);
        // create button columns
        new ButtonColumn(table_Qeustion, edit, COLUMN_EDIT);
        new ButtonColumn(table_Qeustion, delete, COLUMN_DELETE);
    }

    // on update filuit
    @Override
    public void update(Observable o, Object arg) {
        fillUI();
    }


    /**
     * Get data from model and set in the textfields
     */
    private void fillUI() {
        tModel.setRowCount(0);
        IoTmodel model = IoTmodel.getInstance();

        for (ConfigQuestion q : model.getQuestions()) {
            Object[] ob = createRow(q);

            tModel.addRow(ob);
        }
        textField_EndTime.setText(model.getEndConfig().getValue() + "");
        textField_StartTime.setText(model.getStartConfig().getValue() + "");
        textField_intervalQuestion.setText(model.getQuestionConfig().getValue() + "");
        textField_IntervalTime.setText(model.getMeasurementsConfig().getValue() + "");
        for (ConfigItem<Boolean> day : model.getDayConfig()) {
            switch (day.getKey()) {
                case "ma":
                    checkbox_ma.setSelected(day.getValue()); break;
                case "di":
                    checkbox_di.setSelected(day.getValue());break;
                case "wo":
                    checkbox_wo.setSelected(day.getValue());break;
                case "do":
                    checkbox_do.setSelected(day.getValue());break;
                case "vr":
                    checkbox_vr.setSelected(day.getValue());break;
                case "za":
                    checkbox_za.setSelected(day.getValue());break;
                case "zo":
                    checkbox_zo.setSelected(day.getValue());break;
                default:
                    break;

            }
        }
    }

    // create question row for the table
    private Object[] createRow(ConfigQuestion q) {
        Object[] result = new Object[tModel.getColumnCount()];

        result[COLUMN_ID] = q.getKey();
        result[COLUMN_QUESTION] = q.getQuestion();
        result[COLUMN_EDIT] = new ImageIcon(getClass().getResource("/resource/config/pencil.png"));
        result[COLUMN_DELETE] = new ImageIcon(getClass().getResource("/resource/config/close.png"));
        return result;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateModel();
        IoTmodel iotModel = IoTmodel.getInstance();
        String action = e.getActionCommand();
        if(action.equals(button_Save.getActionCommand())){
            saveConfig();
        } else if (action.equals(button_AddQuestion.getActionCommand())) {
            ConfigQuestion q = IoTmodel.getInstance().createQuestion();
            EditQuestion dialog = new EditQuestion(iotModel.getFrame(),true, q);
            dialog.pack();
            dialog.setVisible(true);
        } else if(action.equals(button_LoadLocalFile.getActionCommand())){
            loadConfigFile();
        }
        try{
            if(action.equals(button_SendArduino.getActionCommand())) {
                if(iotModel.checkConfig()){
                    new Thread_SendConfig(iotModel.getComPort()).start();
                } else {
                    JOptionPane.showMessageDialog(iotModel.getFrame(), "De instellingen zijn onjuist.\nZorg dat alle velden een geldige waarde hebben.\n\n" +
                            "Mogelijke fouten zijn:\n" +
                            " - Er moet minimaal 1 vraag ingevuld zijn.\n" +
                            " - Elke vraag moet minimaal 1 antwoord hebben.\n" +
                            " - De regels van de vraag en antwoordmogelijkheden mogen niet meer dan 17 tekens bevatten (inclusief spaties).\n");
                }




            } else if (action.equals(button_DownloadFromArduino.getActionCommand())){
                // for some reason  running this in a threads causes an exception. Simple solution run it on the main thread.
                new Thread_ReadConfig(IoTmodel.getInstance().getComPort()).run();
            }
        } catch (Exception ex){

        }

    }


    /**
     * Load config file
     */
    private void loadConfigFile() {
        try {
            File dir = IoTmodel.getInstance().getConfigDir();
            IoTmodel.getInstance().readConfig(new File(dir, "local_config.ini"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Laen mislukt\n\n"+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save config file
     */
    private void saveConfig() {
        try{
            IoTmodel model = IoTmodel.getInstance();
            File configDir = model.getConfigDir();

            File configFile = new File(configDir, "local_config.ini");

            FileOutputStream stream = new FileOutputStream(configFile);

            ArrayList<byte[]> configData = model.createConfigFile();
            for(byte[] buff : configData){
                stream.write(buff);
            }
            stream.flush();
            stream.close();


        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Opslaan mislukt\n\n"+e.getMessage());
        }



    }

    /**
     * Update the model.
     *
     */
    private void updateModel() {
        IoTmodel m = IoTmodel.getInstance();
        m.getMeasurementsConfig().setValue((Integer.parseInt(!textField_IntervalTime.getText().equals("") ? textField_IntervalTime.getText() : "0")));

        m.getQuestionConfig().setValue((Integer.parseInt(!textField_intervalQuestion.getText().equals("") ? textField_intervalQuestion.getText() : "0")));
        m.getStartConfig().setValue((Integer.parseInt(!textField_StartTime.getText().equals("") ? textField_StartTime.getText() : "0")));
        m.getEndConfig().setValue((Integer.parseInt(!textField_EndTime.getText().equals("") ? textField_EndTime.getText() : "0")));



        for (ConfigItem<Boolean> day : m.getDayConfig()) {
            switch (day.getKey()) {
                case "ma":
                    day.setValue(checkbox_ma.isSelected());
                    break;
                case "di":
                    day.setValue(checkbox_di.isSelected());
                    break;
                case "wo":
                    day.setValue(checkbox_wo.isSelected());
                    break;
                case "do":
                    day.setValue(checkbox_do.isSelected());
                    break;
                case "vr":
                    day.setValue(checkbox_vr.isSelected());
                    break;
                case "za":
                    day.setValue(checkbox_za.isSelected());
                    break;
                case "zo":
                    day.setValue(checkbox_zo.isSelected());
                    break;
                default:
                    break;

            }
        }
    }



    private class QuestionTableModel extends DefaultTableModel{
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == COLUMN_EDIT || column ==COLUMN_DELETE;
        }
    }
}
