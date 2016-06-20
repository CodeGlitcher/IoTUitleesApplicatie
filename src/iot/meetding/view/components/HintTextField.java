package iot.meetding.view.components;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;

/**
 * Custom textfield that displays a hint text
 */
public class HintTextField extends JTextField {

    private final String _hint;


    public HintTextField(String hint, InputVerifier v){
        _hint = hint;
        setInputVerifier(v);
    }
    public HintTextField(String hint){
        _hint = hint;
        setToolTipText(hint);
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getText().length() == 0) {
            int h = getHeight();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >> 1) + ((c1 & m) >> 1);
            g.setColor(new Color(c2, true));
            g.drawString(_hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
        }
    }


}

