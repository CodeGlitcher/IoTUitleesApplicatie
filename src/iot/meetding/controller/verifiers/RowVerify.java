package iot.meetding.controller.verifiers;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rob on 7-6-2016.
 *
 */
public class RowVerify extends InputVerifier {

    public static final int MAX_LENGTH = 50;

    @Override
    public boolean verify(JComponent input) {
        if(input instanceof  JTextField){

            String in = ((JTextField) input).getText();
            System.out.println(in.length());
            if(in.length() <=  MAX_LENGTH){
                input.setBackground(Color.WHITE);
                return true;
            } else {
                input.setBackground(Color.RED);
                return false;
            }
        }
        throw new VerifyError("Wrong input component");
    }
}