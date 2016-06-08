package iot.meetding.controller.verifiers;

import javax.swing.*;

/**
 * Created by Rob on 7-6-2016.
 *
 */
public class QuestionVerify  extends InputVerifier {

    private final int MAX_LENGTH = 50;

    @Override
    public boolean verify(JComponent input) {
        if(input instanceof  JTextField){
            String in = ((JTextField) input).getText();
            return in.length() <=  MAX_LENGTH;
        }
        throw new VerifyError("Wrong input component");
    }
}
