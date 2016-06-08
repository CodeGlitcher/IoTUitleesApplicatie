package iot.meetding.controller;

import iot.meetding.Logger;
import iot.meetding.view.beans.ConfigQuestion;
import iot.meetding.view.components.ListQuestion;
import iot.meetding.view.beans.ConfigItem;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rob on 29-5-2016.
 *
 */
public class ListConfigRender implements ListCellRenderer<ConfigQuestion> {
    @Override
    public Component getListCellRendererComponent(JList<? extends ConfigQuestion> list, ConfigQuestion value, int index, boolean isSelected, boolean cellHasFocus) {
        return new ListQuestion(value).getPanel();
    }



}
