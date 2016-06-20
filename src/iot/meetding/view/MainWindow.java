package iot.meetding.view;

import iot.meetding.model.IoTmodel;
import iot.meetding.view.beans.WindowDataReadArduino;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rob on 13-5-2016.
 */
public class MainWindow extends JFrame implements ActionListener, Observer {

    // Generated code. Should not be changed
    private JComboBox<String> comboBox_comPorts;
    private JButton button_readArduino;
    private JProgressBar progressbar_status;
    private JButton button_refresh;
    private JPanel main;
    private JTextArea textArea_output;
    private JCheckBox checkBox_appendCSV;
    private JTabbedPane mainPanel;
    private JScrollPane scrollPane_scrollTextArea;
    private JPanel t;

    private IoTmodel model;
    private WindowDataReadArduino data_read_window;

    public MainWindow() {
        data_read_window = new WindowDataReadArduino();
        data_read_window.addObserver(this);
        model = IoTmodel.getInstance();
        model.addObserver(this);
        setContentPane(main);
        setSize(500, 500);

        // set auto scroll of output
        DefaultCaret caret = (DefaultCaret) textArea_output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        // setup action listeners
        button_refresh.addActionListener(this);
        button_readArduino.addActionListener(this);
        checkBox_appendCSV.addActionListener(this);

        // if window is closed, end application (default is hide window)
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        comboBox_comPorts.addActionListener(this);
    }

    /**
     * Handle actions
     *
     * @param e, ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(button_refresh.getActionCommand())) {
            data_read_window.clearLogData();
            comboBox_comPorts.removeAllItems();
            model.updateComPorts(data_read_window);
        } else if (action.equals(button_readArduino.getActionCommand())) {
            data_read_window.clearLogData();
            model.startReadData((String) comboBox_comPorts.getSelectedItem(), data_read_window);
        } else if (action.equals(checkBox_appendCSV.getActionCommand())) {
            data_read_window.setAppendCSV(checkBox_appendCSV.isSelected());
        } else if (action.equals(comboBox_comPorts.getActionCommand())){
            model.setComPort((String)comboBox_comPorts.getSelectedItem());
        }
    }

    // update to data
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof WindowDataReadArduino) {

            textArea_output.setText(data_read_window.getLogData());
            progressbar_status.setMaximum(data_read_window.getFileSize());
            progressbar_status.setValue(data_read_window.getProgress());
        }
        if (o instanceof IoTmodel && arg instanceof String) {
            comboBox_comPorts.addItem((String) arg);
        }

    }

}
