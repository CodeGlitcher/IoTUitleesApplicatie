package iot.meetding.view;

import iot.meetding.controller.ButtonColumnRow3;
import iot.meetding.controller.verifiers.RowVerify;
import iot.meetding.view.beans.ConfigQuestion;
import iot.meetding.view.components.HintTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.ArrayList;

import static iot.meetding.controller.verifiers.RowVerify.MAX_LENGTH;

public class EditQuestion extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private HintTextField textField_QuestionPart1;
    private HintTextField textField_QuestionPart2;
    private HintTextField textField_QuestionPart3;
    private HintTextField textField_QuestionPart4;
    private JLabel label_Qeustion1;
    private JLabel label_Qeustion2;
    private JLabel label_Qeustion3;
    private JLabel label_Qeustion4;
    private JButton buttton_AddAnswer;
    private JTable table_answers;

    private DefaultTableModel tModel;

    private ConfigQuestion question;


    public EditQuestion(ConfigQuestion question) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        this.question = question;

        // Add action listeners
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

        // create a new answer button
        buttton_AddAnswer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAnswer();
            }
        });

        // Set Listeners for textfields
        textField_QuestionPart1.getDocument().addDocumentListener(new QuestionListener());
        textField_QuestionPart2.getDocument().addDocumentListener(new QuestionListener());
        textField_QuestionPart3.getDocument().addDocumentListener(new QuestionListener());
        textField_QuestionPart4.getDocument().addDocumentListener(new QuestionListener());

        // set text
        textField_QuestionPart1.setText(question.getQeustionPart(0));
        textField_QuestionPart2.setText(question.getQeustionPart(1));
        textField_QuestionPart3.setText(question.getQeustionPart(2));
        textField_QuestionPart4.setText(question.getQeustionPart(3));

        /// create table
        tModel = new AnswerTableModel();
        table_answers.setModel(tModel);
        tModel.addColumn("Antwoord");
        tModel.addColumn("Verwijder");
        tModel.addColumn("");
        table_answers.setVisible(true);
        table_answers.setFillsViewportHeight(true);

        // action vor delete answer
        Action delete = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int modelRow = Integer.valueOf(e.getActionCommand());
                tModel.removeRow(modelRow); // remove 3 rows
                tModel.removeRow(modelRow);
                tModel.removeRow(modelRow);
            }
        };
        // add button to table
        new ButtonColumnRow3(table_answers, delete, 2);

        // if a cell value changes, recalculate row length
        tModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = table_answers.getSelectedRow();

                // table change but no row selected
                if(row < 0){ // do nothing
                    return;
                }
                // resul is the new value to insert in the DB
                String result = table_answers.getValueAt(row, 0).toString();
                // update is my method to update. Update needs the id for
                // the where clausule. resul is the value that will receive
                // the cell and you need column to tell what to update.
                String newValue = String.format("(%d/%d)",result.length(), MAX_LENGTH);
                String curr = table_answers.getValueAt(row, 2).toString();
                // setting the value will trigger a new update event.
                // only set value when it is different to prevent stackoverflow error
                if(!newValue.equals(curr)){
                    tModel.setValueAt(newValue, row , 1 );
                }
            }
        });
        table_answers.getTableHeader().setReorderingAllowed(false);
        table_answers.getColumnModel().getColumn(0).setPreferredWidth(150);
        table_answers.getColumnModel().getColumn(1).setPreferredWidth(50);
        table_answers.getColumnModel().getColumn(2).setPreferredWidth(50);

        // add answers to table
        question.getAnswers().forEach(this::addAnswer);

        table_answers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable source = (JTable)e.getSource();
                int row = source.rowAtPoint( e.getPoint() );
                int column = source.columnAtPoint( e.getPoint() );
                if(column == 0){
                    table_answers.editCellAt(row, 0);
                }
                super.mouseClicked(e);
            }
        });
    }


    /**
     * Add empty answer
     */
    private void addAnswer() {
        String[] newAnswer = new String[3];
        newAnswer[0] ="";
        newAnswer[1] = "";
        newAnswer[2] = "";
        addAnswer(newAnswer);
    }

    /**
     * Add existing answer
     * @param answer array with length 3
     */
    private void addAnswer(String [] answer){
        if(answer.length != 3){
            throw new IndexOutOfBoundsException("Answer length incorrect");
        }
        Object[] row = new Object[3];
        row[0] = answer[0];

        row[1] = String.format("(%d/%d)",row[0].toString().length(),  MAX_LENGTH);
        row[2] = new ImageIcon(getClass().getResource("/resource/config/close.png"));
        tModel.addRow(row);

        row = new Object[3];
        row[0] = answer[1];
        row[1] = String.format("(%d/%d)",row[0].toString().length(),  MAX_LENGTH);
        row[2] = null;
        tModel.addRow(row);

        row = new Object[3];
        row[0] = answer[2];
        row[1] = String.format("(%d/%d)",row[0].toString().length(),  MAX_LENGTH);
        row[2] = null;
        tModel.addRow(row);



    }


    /**
     * Ok function
     * Get data from input fields and update the model
     */
    private void onOK() {
        question.setQuestion(0, textField_QuestionPart1.getText());
        question.setQuestion(1, textField_QuestionPart2.getText());
        question.setQuestion(2, textField_QuestionPart3.getText());
        question.setQuestion(3, textField_QuestionPart4.getText());

        ArrayList<String[]> newAnswers = new ArrayList<>();
        String[] answer = new String[2];

        int totAnswers = tModel.getRowCount() / 3;
        int index = 0;
        for(int i = 0; i < totAnswers; i++){
            answer = new String[3];
            answer[0] = tModel.getValueAt(index, 0).toString();
            answer[1] = tModel.getValueAt(index+1, 0).toString();
            answer[2] = tModel.getValueAt(index+2, 0).toString();
            newAnswers.add(answer);
            index += 3;
        }
        question.setAnswers(newAnswers);

        dispose();
    }

    /**
     * Cancel event
     */
    private void onCancel() {
        // update nothing
        dispose();
    }

    public static void main(String[] args) {
        EditQuestion dialog = new EditQuestion(new ConfigQuestion());
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        textField_QuestionPart1 = new HintTextField("De eerste regel van de vraag", new RowVerify());
        textField_QuestionPart2 = new HintTextField("De tweede regel van de vraag", new RowVerify());
        textField_QuestionPart3 = new HintTextField("De derde regel van de vraag", new RowVerify());
        textField_QuestionPart4 = new HintTextField("De vierde regel van de vraag", new RowVerify());
    }


    private void updateLabels() {
        label_Qeustion1.setText(String.format("(%d/%d)", textField_QuestionPart1.getText().length(), MAX_LENGTH));
        label_Qeustion2.setText(String.format("(%d/%d)", textField_QuestionPart2.getText().length(), MAX_LENGTH));
        label_Qeustion3.setText(String.format("(%d/%d)", textField_QuestionPart3.getText().length(), MAX_LENGTH));
        label_Qeustion4.setText(String.format("(%d/%d)", textField_QuestionPart4.getText().length(), MAX_LENGTH));
    }



    private class QuestionListener implements DocumentListener{

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLabels();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLabels();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateLabels();
        }

    }


    public class AnswerTableModel extends DefaultTableModel {


        /**
         * @param row
         * @param column
         * @return
         */
        @Override
        public boolean isCellEditable(int row, int column){
            return column == 0 || column == 2 && row % 3 == 0;
        }

    }
}

