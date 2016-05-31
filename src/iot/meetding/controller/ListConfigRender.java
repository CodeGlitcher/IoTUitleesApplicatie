package iot.meetding.controller;

import iot.meetding.Logger;
import iot.meetding.view.ListQuestion;
import iot.meetding.view.beans.ConfigItem;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rob on 29-5-2016.
 */
public class ListConfigRender implements ListCellRenderer<ConfigItem> {
    @Override
    public Component getListCellRendererComponent(JList<? extends ConfigItem> list, ConfigItem value, int index, boolean isSelected, boolean cellHasFocus) {
        Logger.log("hoi " + index);
        return new ListQuestion().test(index);
    }
}
