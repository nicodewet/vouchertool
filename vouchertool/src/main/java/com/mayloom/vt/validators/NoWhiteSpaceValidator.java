package com.mayloom.vt.validators;

import com.vaadin.data.validator.RegexpValidator;

/**
 * String validator for strings that should not contain white space.
 * 
 * @author Nico
 */
@SuppressWarnings("serial")
public class NoWhiteSpaceValidator extends RegexpValidator {

    /**
     * Creates a validator for checking that a string contains no white space.
     * 
     * @param errorMessage
     *            the message to display in case the value does not validate.
     */
    public NoWhiteSpaceValidator(String errorMessage) {
        super(
        		//        		^                 # start-of-string
        		//        		(?=\S+$)          # no whitespace allowed in the entire string
        		//  			.*
        		//        		$                 # end-of-string
        		"^(?=\\S+$).*$",
                true, errorMessage);
    }
}
