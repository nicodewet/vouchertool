package com.mayloom.vouchserv.util;

import java.util.Date;
import org.joda.time.DateTime;

/**
 * @author Nico
 *
 * Although a utility class, this class is predominantly a Joda-Time wrapper which has
 * a far more convenient API than java.util.Date
 */
public class DateUtil {

	public static final Date getDateYearFromNow() {
		DateTime now = new DateTime();
		DateTime yearFromNow = now.plusYears(1);
		return yearFromNow.toDate();
	}
	
	public static final boolean isDateInPast(Date dateToCheckIfInPast) {
		
		if (dateToCheckIfInPast == null) {
			return false;
		}
		
		DateTime dateTimeToCheckIfInPast = new DateTime(dateToCheckIfInPast);
		return !dateTimeToCheckIfInPast.isAfterNow();
	}
	
}
