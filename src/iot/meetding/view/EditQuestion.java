package iot.meetding.view;

import iot.meetding.controller.ButtonColumnRow3;
import iot.meetding.controller.verifiers.RowVerify;
import iot.meetding.view.beans.ConfigQuestion;
import iot.meetding.view.components.HintTextField;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static iot.meetding.controller.verifiers.RowVerify.MAX_LENGTH;

/**
 * EditQuestion dialog
 */
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

    private int COLUMN_ANSWER =0;
    private int COLUMN_CHARS = 1;
    private int COLUMN_BUTTON =2;

    private DefaultTableModel tModel;

    private ConfigQuestion question;

    public EditQuestion(Frame x ,boolean modal,ConfigQuestion question) {
        super(x,modal);
        __construct(question);

    }

    /**
     * Constructor for creating interface.
     * @param question, the question to display
     */
    private void __construct(ConfigQuestion question){
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setSize(500,600);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int x = (screenSize.width - getWidth()) / 2;
        final int y = (screenSize.height - getHeight()) / 2;
        setLocation(x, y);
        this.question = question;
        addActionListener();


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
        createTable(question);
        updateLabels();
    }


    private void addActionListener() {
        // Add action listeners
        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // create a new answer button
        buttton_AddAnswer.addActionListener(e -> addAnswer());
    }

    /**
     * Create table
     * @param question, the question
     */
    private void createTable(ConfigQuestion question) {
        /// create table
        tModel = new AnswerTableModel();
        table_answers.setModel(tModel);
        tModel.addColumn("Antwoord");
        tModel.addColumn("");
        tModel.addColumn("Verwijder");
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
        new ButtonColumnRow3(table_answers, delete, COLUMN_BUTTON, ConfigQuestion.ROWS_QUESTION);

        // if a cell value changes, recalculate row length
        tModel.addTableModelListener(e -> {
            int row = table_answers.getSelectedRow();

            // table change but no row selected
            if(row < 0){ // do nothing
                return;
            }
            // resul is the new value to insert in the DB
            String result = table_answers.getValueAt(row, COLUMN_ANSWER).toString();
            // update is my method to update. Update needs the id for
            // the where clausule. resul is the value that will receive
            // the cell and you need column to tell what to update.
            String newValue = String.format("(%d/%d)",result.length(), MAX_LENGTH);
            String curr = table_answers.getValueAt(row, COLUMN_CHARS).toString();
            // setting the value will trigger a new update event.
            // only set value when it is different to prevent stackoverflow error
            if(!newValue.equals(curr)){
                tModel.setValueAt(newValue, row , COLUMN_CHARS );
            }
        });
        table_answers.getTableHeader().setReorderingAllowed(false);
        table_answers.getColumnModel().getColumn(COLUMN_ANSWER).setPreferredWidth(150);
        table_answers.getColumnModel().getColumn(COLUMN_CHARS).setMaxWidth(60);
        table_answers.getColumnModel().getColumn(COLUMN_BUTTON).setMaxWidth(80);

        // add answers to table
        question.getAnswers().forEach(this::addAnswer);

        table_answers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable source = (JTable)e.getSource();
                int row = source.rowAtPoint( e.getPoint() );
                int column = source.columnAtPoint( e.getPoint() );
                if(column == COLUMN_ANSWER){
                    table_answers.editCellAt(row, COLUMN_ANSWER);
                }
                super.mouseClicked(e);
            }
        });
    }


    /**
     * Add empty answer
     */
    private void addAnswer() {
        String[] newAnswer = new String[ConfigQuestion.ROWS_ANSWER];
        for(int i =0; i< newAnswer.length; i++){
            newAnswer[i]  = "";
        }
        addAnswer(newAnswer);
    }

    /**
     * Add existing answer
     * @param answer array with length 3
     */
    private void addAnswer(String [] answer){
        if(answer.length != ConfigQuestion.ROWS_ANSWER){
            throw new IndexOutOfBoundsException("Answer length incorrect");
        }


        Object[] row;

        for (String anAnswer : answer) {
            row = new Object[tModel.getColumnCount()];
            row[COLUMN_ANSWER] = anAnswer;
            row[COLUMN_CHARS] = String.format("(%d/%d)", row[0].toString().length(), MAX_LENGTH);
            row[COLUMN_BUTTON] = new ImageIcon(getClass().getResource("/resource/config/close.png"));
            tModel.addRow(row);
        }



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
        String[] answer;

        int totAnswers = tModel.getRowCount() / ConfigQuestion.ROWS_ANSWER;
        int index = 0;
        for(int i = 0; i < totAnswers; i++){
            answer = new String[ConfigQuestion.ROWS_ANSWER];
            for(int x = 0; x< answer.length; x++){
                answer[x] = tModel.getValueAt(index+x,COLUMN_ANSWER).toString();
            }
            newAnswers.add(answer);
            index += ConfigQuestion.ROWS_ANSWER;
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


    /**
     * create custom ui components
     */
    private void createUIComponents() {
        textField_QuestionPart1 = new HintTextField("De eerste regel van de vraag", new RowVerify());
        textField_QuestionPart2 = new HintTextField("De tweede regel van de vraag", new RowVerify());
        textField_QuestionPart3 = new HintTextField("De derde regel van de vraag", new RowVerify());
        textField_QuestionPart4 = new HintTextField("De vierde regel van de vraag", new RowVerify());
    }


    /**
     * Update labels to display amount of characters
     */
    private void updateLabels() {
        label_Qeustion1.setText(String.format("(%d/%d)", textField_QuestionPart1.getText().length(), MAX_LENGTH));
        label_Qeustion2.setText(String.format("(%d/%d)", textField_QuestionPart2.getText().length(), MAX_LENGTH));
        label_Qeustion3.setText(String.format("(%d/%d)", textField_QuestionPart3.getText().length(), MAX_LENGTH));
        label_Qeustion4.setText(String.format("(%d/%d)", textField_QuestionPart4.getText().length(), MAX_LENGTH));
    }


    /**
     * Listener for updating labels
     */
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


    /**
     * TableModel class for answer table
     */
    private class AnswerTableModel extends DefaultTableModel {


        /**
         * Checks if a cell is editable
         * @param row, the row
         * @param column, the column
         * @return true|false
         */
        @Override
        public boolean isCellEditable(int row, int column){
            return column == COLUMN_ANSWER || column == COLUMN_BUTTON && row % ConfigQuestion.ROWS_ANSWER == 0;
        }

    }
}

