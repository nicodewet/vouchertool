package com.mayloom.vt.validators;

import com.vaadin.data.validator.AbstractStringValidator;

@SuppressWarnings("serial")
public class PositiveIntegerRangeValidator extends AbstractStringValidator {

	private int LOWER_BOUND = 1;
	private int UPPER_BOUND = 100000;
	
	public PositiveIntegerRangeValidator(int lowerBound, int upperBound) {
		super("{0} is not between " + lowerBound + " and " + upperBound);
		LOWER_BOUND = lowerBound;
		UPPER_BOUND = upperBound;
	}

	@Override
	protected boolean isValidString(String value) {
		String trimmedValue = value.trim();
		int parsedIntVal = -1;
		try {
			parsedIntVal = Integer.parseInt(trimmedValue);
		} catch (NumberFormatException e) {
			return false;
		}
		if (parsedIntVal < LOWER_BOUND || parsedIntVal > UPPER_BOUND) {
			return false;
		} else { 
			return true;
		}
	}
}
