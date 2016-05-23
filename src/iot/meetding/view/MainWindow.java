package iot.meetding.view;

import iot.meetding.model.IoTmodel;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Created by Rob on 13-5-2016.
 *
 */
public class MainWindow extends JFrame implements ActionListener, Observer {


    private JTabbedPane tabbedPane1;
    private JComboBox<String> comboBox_comPorts;
    private JButton button_readArduino;
    private JProgressBar progressbar_status;
    private JButton button_refresh;
    private JPanel main;
    private JTextArea textArea_output;
    private JScrollPane scrollPane_scrollTextArea;

    private IoTmodel model;

    public MainWindow() {
        model = IoTmodel.getInstance();
        model.addObserver(this);
        setContentPane(main);
        setSize(500,500);
        DefaultCaret caret = (DefaultCaret)textArea_output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        button_refresh.addActionListener(this);
        comboBox_comPorts.addActionListener(this);

    }

    private void resetComPorts() {
        comboBox_comPorts.removeAllItems();
        Set<String> ports = null;
        for(String port : ports){
            comboBox_comPorts.addItem(port);
        }
    }



    public void setData(SerialOut data) {
        textArea_output.append(data.getText() + "\n");

    }

    public void getData(SerialOut data) {
        data.setText(textArea_output.getText());
    }

    public boolean isModified(SerialOut data) {
        return data.getText().equals(textArea_output.getText());
    }


    /**
     * Handle actions
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if(action.equals(button_refresh.getActionCommand())){
            comboBox_comPorts.removeAllItems();
            model.updateComPorts();
        } else if (action.equals(comboBox_comPorts.getActionCommand())){
            // TODO
            System.out.println("select");
        } else if(action.equals(button_readArduino.getActionCommand())){
            // TODO
        } // TODO add more
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("123");
        if(o instanceof IoTmodel && arg instanceof String){
            comboBox_comPorts.addItem((String)arg);
        }
    }
}
