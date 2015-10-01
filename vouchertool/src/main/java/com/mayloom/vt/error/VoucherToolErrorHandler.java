/**
 * 
 */
package com.mayloom.vt.error;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Validator;
import com.vaadin.terminal.gwt.server.ChangeVariablesErrorEvent;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractComponent.ComponentErrorEvent;
import com.vaadin.ui.AbstractComponent.ComponentErrorHandler;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;

/**
 * @author Nico
 *
 */
public class VoucherToolErrorHandler implements ComponentErrorHandler {
	
	private static Logger logger = LoggerFactory.getLogger(VoucherToolErrorHandler.class);
	
	/**
	 * com.vaadin.event.ListenerMethod.MethodException
	 * org.springframework.transaction.CannotCreateTransactionException
	 * javax.persistence.PersistenceException
	 * org.hibernate.exception.JDBCConnectionException
	 * org.postgresql.util.PSQLException
	 * java.net.ConnectException
	 */

	/* (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent.ComponentErrorHandler#handleComponentError(com.vaadin.ui.AbstractComponent.ComponentErrorEvent)
	 */
	public boolean handleComponentError(ComponentErrorEvent event) {
		
		logger.error("Component error being handled: {}", event.toString());
		
		if (event instanceof ChangeVariablesErrorEvent) {
			 
             Throwable cause = event.getThrowable() == null ? null : event.getThrowable().getCause();
             
             Component owner = ((ChangeVariablesErrorEvent)event).getComponent();
             
             if (cause != null && owner instanceof AbstractComponent) {
            	 
            	 AbstractComponent component = (AbstractComponent)owner;
                 
                 if (component instanceof AbstractField) {
                     
                	 AbstractField field = (AbstractField) component;
                     
                     logger.error("Removing Component Error Stack Trace from UI. Cause of exception: {}", ExceptionUtils.getFullStackTrace(cause));
                     
                     field.setCurrentBufferedSourceException(null);
                 }
            	 
                 String errorMessage = "An error occurred";
                 
            	 if (cause instanceof com.vaadin.data.Property.ConversionException) {
                     
            		 errorMessage = "Incorrect Format";
            		 
            	 } 
            	 
//            	 else if (cause instanceof org.springframework.transaction.CannotCreateTransactionException) {
//            		 
//            		 errorMessage = errorMessage + " - please try again later";
//            		 
//            	 } 
            	 
            	 else {
                	
            		 logger.error("No specific error message available for this Exception: {}", cause.getClass().getName());
            		 
            	 }
            	
            	 component.setComponentError(new Validator.InvalidValueException(errorMessage));
                 return true; 
            	 
             }
             
        } 
        return false;
	}

}
