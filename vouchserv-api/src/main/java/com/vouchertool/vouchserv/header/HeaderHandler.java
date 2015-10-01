package com.vouchertool.vouchserv.header;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
*
* @author www.javadb.com
*/
public class HeaderHandler implements SOAPHandler<SOAPMessageContext> {
	
	private static final Properties config;
	
	static {
		//load a properties file from class path, inside static method
		config = new Properties();
		try {
			config.load(HeaderHandler.class.getClassLoader().getResourceAsStream("vouchserv-api.properties"));
			if (config.get("vouchserv.username") == null) {
				throw new RuntimeException("No vouchserv.username");
			}
			if (config.get("vouchserv.password") == null) {
				throw new RuntimeException("No vouchserv.password");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean handleMessage(SOAPMessageContext smc) {

		Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outboundProperty.booleanValue()) {

			SOAPMessage message = smc.getMessage();

           try {

        	   SOAPEnvelope envelope = smc.getMessage().getSOAPPart().getEnvelope();
               SOAPHeader header = envelope.addHeader();

               SOAPElement security =
                       header.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

               SOAPElement usernameToken =
                       security.addChildElement("UsernameToken", "wsse");
               usernameToken.addAttribute(new QName("xmlns:wsu"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

               SOAPElement username =
                       usernameToken.addChildElement("Username", "wsse");
               username.addTextNode(config.getProperty("vouchserv.username"));

               SOAPElement password =
                       usernameToken.addChildElement("Password", "wsse");
               password.setAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
               password.addTextNode(config.getProperty("vouchserv.password"));

               //Print out the outbound SOAP message to System.out
               // message.writeTo(System.out);
               // System.out.println("");
               
           } catch (Exception e) {
               e.printStackTrace();
           }

       } else {
           try {
               
               //This handler does nothing with the response from the Web Service so
               //we just print out the SOAP message.
               SOAPMessage message = smc.getMessage();
               // message.writeTo(System.out);
               // System.out.println("");

           } catch (Exception ex) {
               ex.printStackTrace();
           }
       }


       return outboundProperty;

   }

   public Set getHeaders() {
       //throw new UnsupportedOperationException("Not supported yet.");
       return null;
   }

   public boolean handleFault(SOAPMessageContext context) {
       //throw new UnsupportedOperationException("Not supported yet.");
       return true;
   }

   public void close(MessageContext context) {
   //throw new UnsupportedOperationException("Not supported yet.");
   }
}

