package com.mayloom.vouchserv.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mayloom.vouchserv.dbo.DatabaseHelper;

/**
 * Used to add an initial user during prototyping phase.
 * 
 * @author Nico
 */
public class UserConfig {
	
	private final Logger logger = LoggerFactory.getLogger(UserConfig.class);
	
	@Autowired
	@Qualifier("databaseHelper")
	private DatabaseHelper databaseHelper;
	
	private static final String INIT_USERNAME = "nico@nicodewet.com";
	private static final String INIT_PASSWORD = "password";
	
	public void init() {
		
		logger.info("==================== UserConfig =========================");
        if (this.databaseHelper == null) {
            throw new IllegalStateException("The [databaseHelper] property must be set.");
        }
        if (!databaseHelper.doesUserExist(INIT_USERNAME)) {
        	databaseHelper.addNewOrdinaryUser(INIT_USERNAME, INIT_PASSWORD);
        	logger.info("Added INIT user {}", INIT_USERNAME);
        }
		
	}

}
