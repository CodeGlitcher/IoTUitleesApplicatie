package iot.meetding.controller.verifiers;

import javax.swing.*;

/**
 * Created by Rob on 1-6-2016.
 * Input verifier for integers
 */
public class IntVerify extends InputVerifier {
    /**
     * Verify input
     * Empty Strings allowed
     * Throws error if input is not a TextField
     * @param input; the input field
     * @return boolean
     */
    @Override
    public boolean verify(JComponent input) {
        if(input instanceof  JTextField){
            String in = ((JTextField) input).getText();
            if(in.isEmpty()){
                return true;
            }
            try{
                Integer.parseInt(in);
                return true;
            } catch (NumberFormatException e){
                return false;
            }
        }
        throw new VerifyError("Wrong input component");
    }
}
