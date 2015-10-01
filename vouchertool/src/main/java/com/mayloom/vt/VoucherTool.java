package com.mayloom.vt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mayloom.vt.spring.DAO;
import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * The Application's "main" class
 * 
 * Vaadin framework associates requests with sessions so that 
 * an application class instance is really a session object.
 */
@SuppressWarnings("serial")
public class VoucherTool extends Application {
	
    private Window mainWindow;
    
    private static Logger logger = LoggerFactory.getLogger(VoucherTool.class);
    
    public static final Properties properties = loadFromPropertiesFile("vouchertool.properties");
    public static final String VOUCHER_TOOL_VERSION = properties.getProperty("application.version");
    public static boolean IS_RUNNING_IN_PROD_MODE = isProdMode();
    
	@Override
    public void terminalError(Terminal.ErrorEvent event) {
       
    	// does it make sense to use the mainWindow here?
    	mainWindow.showNotification("An internal error has occurred", Notification.TYPE_ERROR_MESSAGE);
        
        logger.error("An unhandled exception occurred: {}", ExceptionUtils.getFullStackTrace(event.getThrowable()));
    }
     
    private static boolean isProdMode() {
    	String mode = properties.getProperty("application.environment");
    	logger.info("Running in {} mode", mode);
    	if ("prod".equals(mode)) {
    		return true;
    	} else {
    		return false;
    	}
	}

	@Override
    public void init()
    {
        mainWindow = new Window(" VoucherTool: Unique Code Generator & Voucher Management Platform");
        setMainWindow(mainWindow);
        
        LoginView loginView = new LoginView();
        
        mainWindow.setContent(loginView);
         
		setTheme("vouchertooltheme");
		
		DAO.initialize(this);
    }
    
    private static Properties loadFromPropertiesFile(String fileName) {
    	URL resource = VoucherTool.class.getClassLoader().getResource("/" + fileName);
    	File file;
		try {
			file = new File(resource.toURI());
		} catch (URISyntaxException e1) {
			throw new RuntimeException(e1);
		}
    	FileInputStream input;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			throw new RuntimeException(e1);
		}
    	Properties properties = new Properties();
    	try {
    	  properties.load(input);
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    	return properties;
    }
    
}
