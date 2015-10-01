package com.mayloom.vt;

import com.mayloom.vt.validators.NoWhiteSpaceValidator;
import com.vaadin.data.Item;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class CreateAccountFieldFactory implements FormFieldFactory {
	
	private static final long serialVersionUID = -8024164933703825323L;
	
	private static final String VAL_PASSWD_LEN_MESSAGE = "Minimum Password length is 5 characters";
	private static final String VAL_PASSWD_TXT_MESSAGE = "Password cannot contain white space.";

	public Field createField(Item item, Object propertyId, Component uiContext) {
		// Identify the fields by their Property ID.
		String pid = (String) propertyId;
		if ("userName".equals(pid)) {
			 TextField field = new TextField("Username (Email)");
			 field.setRequired(true);
			 field.setRequiredError("Please supply a username");
			 field.addValidator(new EmailValidator("Username must be an email address"));
			 return field;
		} else if ("password".equals(pid)) {
			PasswordField field = new PasswordField("Password");
			field.setRequired(true);
			field.setRequiredError("Please supply a password");
			field.addValidator(new StringLengthValidator(VAL_PASSWD_LEN_MESSAGE, 5, 999, false));
			field.addValidator(new NoWhiteSpaceValidator(VAL_PASSWD_TXT_MESSAGE));
			return field;
		} else if ("confirmPassword".equals(pid)) {
			PasswordField field = new PasswordField("Confirm Password");
			field.setRequired(true);
			field.setRequiredError("Please confirm your password");
			field.addValidator(new StringLengthValidator(VAL_PASSWD_LEN_MESSAGE, 5, 999, false));
			field.addValidator(new NoWhiteSpaceValidator(VAL_PASSWD_TXT_MESSAGE));
			return field;
		}
		
		return null; // Invalid field (property) name.
	} 
}
