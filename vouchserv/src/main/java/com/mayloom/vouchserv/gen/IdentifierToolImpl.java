/**
 * 
 */
package com.mayloom.vouchserv.gen;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.mayloom.vouchserv.imp.enums.FormattedString;

/**
 * @author Nico
 *
 */
public class IdentifierToolImpl implements IdentifierTool {
	
	private Random random = new Random();
	
	/* (non-Javadoc)
	 * @see com.mayloom.vouchserv.gen.IdentifierTool#generateVSVID()
	 */
	public String generateVSVID() {
			
		DateFormat dateFormat = new SimpleDateFormat(FormattedString.DATE_FORMAT.toString());
		Date now = new Date();
		
		String vsvId = FormattedString.VSVID.toString() + dateFormat.format(now) 
							+ String.format(FormattedString.NINE_DECIMALS_FORMAT.toString(), random.nextInt(999999999));
		
		return vsvId;
		
	}

	/* (non-Javadoc)
	 * @see com.mayloom.vouchserv.gen.IdentifierTool#validateVSVID()
	 */
	public boolean validateVSVID(String vsvId) {
		
		if (vsvId != null && vsvId.length() > 0) {
			
			return vsvId.matches(FormattedString.VSVID_REG_EXP.toString());
			
		} else {
			
			return false;
			
		}
		
	}

}
