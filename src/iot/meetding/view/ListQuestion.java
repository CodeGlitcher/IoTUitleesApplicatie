package iot.meetding.view;

import javax.swing.*;

/**
 * Created by Rob on 29-5-2016.
 *
 */
public class ListQuestion extends JPanel {
    private JButton bewerkButton;
    private JLabel key;
    private JButton button1;
    private JPanel panel;


    public ListQuestion(){
        setVisible(true);

    }
    public JPanel test(int index){
        key.setText(index +"");
        return panel;
    }
}
