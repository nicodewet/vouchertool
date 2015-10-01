package com.mayloom.vt;

import java.util.Date;

import com.mayloom.vt.bean.GenerateAnonymousVouchersBean;
import com.mayloom.vt.validators.PositiveIntegerRangeValidator;
import com.vaadin.data.Item;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.data.validator.IntegerValidator;

public class GenerateAnonymousVouchersFieldFactory implements FormFieldFactory {

	public Field createField(Item item, Object propertyId, Component uiContext) {
		// Identify the fields by their Property ID.
		String pid = (String) propertyId;
		if ("voucherNumber".equals(pid)) {
			 TextField field = new TextField("Number of Unique Codes (1 to 1000)");
			 field.setRequired(true);
			 field.setDescription("The number of unique codes in the batch");
			 field.setRequiredError("Please supply the number of unique codes to generate");
			 field.addValidator(new IntegerValidator("Please only use digits (0-9)"));
			 field.addValidator(new PositiveIntegerRangeValidator(1, 1000));
			 return field;
		} else if ("pinLength".equals(pid)) {
			TextField field = new TextField("Unique Code Length (6 to 30)");
			field.setRequired(true);
			field.setDescription("How many digits or alpanumeric characters in each unique code");
			field.setRequiredError("Please supply how long each unique code should be");
			 field.addValidator(new IntegerValidator("Please only use digits (0-9)"));
			 field.addValidator(new PositiveIntegerRangeValidator(6, 30));
			return field;
		} else if ("pinType".equals(pid)) {
//			field.setDescription("Whether you want digits, alphanumeric characters or both in each unique code");
//			field.setRequiredError("Please supply the type of unique code to generate");			
			ListSelect listSelect = new ListSelect("Unique Code Type");
			listSelect.setInvalidAllowed(false);
			listSelect.setNullSelectionAllowed(false);
			listSelect.setRequired(true);
			listSelect.setMultiSelect(false);
			listSelect.setRows(3);
			listSelect.addItem(GenerateAnonymousVouchersBean.NUMERIC);
			listSelect.addItem(GenerateAnonymousVouchersBean.ALPHA_UPPER);
			listSelect.addItem(GenerateAnonymousVouchersBean.ALPHA_MIXED);
	        return listSelect; 
		} 
		
		return null; // Invalid field (property) name.
	}

}
