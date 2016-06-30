package iot.meetding.controller;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * {@inheritDoc}
 * Extends button column to make only every x  row a button.
 * Other rows remain empty
 */
public class ButtonColumnRow3 extends ButtonColumn
{


    private int buttonRow;
    /**
     *  {@inheritDoc}
     *  @param  buttonRow; 1 button in ever x rows.
     */
    public ButtonColumnRow3(JTable table, Action action, int column, int buttonRow)
    {
       super(table,action,column);
        this.buttonRow = buttonRow;
    }




    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
        System.out.println(row);
        if(row % buttonRow != 0){
            this.editorValue = value;
            return null;
        }

        return super.getTableCellEditorComponent(table,value,isSelected,row,column);
    }

    @Override
    public Object getCellEditorValue()
    {
        return editorValue;
    }

    //
//  Implement TableCellRenderer interface
//
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {

        if(row % buttonRow != 0){
            return null;
        }
        return super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        int row = table.convertRowIndexToModel( table.getEditingRow() );
        if(row % buttonRow != 0){
            fireEditingStopped();
            return;
        }
        super.actionPerformed(e);
    }



}