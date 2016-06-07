package iot.meetding.view;

import iot.meetding.view.components.HintTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EditQuestion extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private HintTextField textField_QuestionPart1;
    private JList list1;
    private HintTextField textField_QuestionPart2;
    private HintTextField textField_QuestionPart3;
    private HintTextField textField_QuestionPart4;
    private JLabel label_Qeustion1;
    private JLabel label_Question2;
    private JLabel label_Qeustion3;
    private JLabel label_Question4;
    private JButton buttton_AddAnswer;

    public EditQuestion() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        EditQuestion dialog = new EditQuestion();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
