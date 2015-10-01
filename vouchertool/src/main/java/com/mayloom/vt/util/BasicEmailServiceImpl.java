package com.mayloom.vt.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicEmailServiceImpl implements BasicEmailService {
	
	private static final String SMTP_HOST_NAME = "smtp.sendgrid.net";
	private static final String SMTP_AUTH_USER = "vouchertool";
	private static final String SMTP_AUTH_PWD  = "4ggplsjeAk";
	private static final String SMTP_PORT = "587";
	
	private static Logger logger = LoggerFactory.getLogger(BasicEmailServiceImpl.class);
	
	private final boolean logOnlyDontSend;
	
	public BasicEmailServiceImpl(boolean logOnlyDontSend) {
		this.logOnlyDontSend = logOnlyDontSend;
	}

	@Override
	public boolean sendBasicEmail(BasicEmail email) {
		if (!logOnlyDontSend) {
			Properties props = new Properties();
	        props.put("mail.transport.protocol", "smtp");
	        props.put("mail.smtp.host", SMTP_HOST_NAME);
	        props.put("mail.smtp.port", SMTP_PORT);
	        props.put("mail.smtp.auth", "true");
	 
	        Authenticator auth = new SMTPAuthenticator();
	        Session mailSession = Session.getDefaultInstance(props, auth);
	        // uncomment for debugging infos to stdout
	        // mailSession.setDebug(true);
	        Transport transport;
			try {
				transport = mailSession.getTransport();
			} catch (NoSuchProviderException e) {
				throw new RuntimeException(e);
			}
	 
	        MimeMessage message = new MimeMessage(mailSession);
	 
	        Multipart multipart = new MimeMultipart("alternative");
	 
	        BodyPart part1 = new MimeBodyPart();
	        try {
				part1.setText(email.getBodyPlain());
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	 
	        BodyPart part2 = new MimeBodyPart();
	        try {
				part2.setContent(email.getBodyHtml(), "text/html");
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	 
	        try {
				multipart.addBodyPart(part1);
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	        try {
				multipart.addBodyPart(part2);
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	 
	        try {
				message.setContent(multipart);
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	        try {
				message.setFrom(new InternetAddress(email.getFrom()));
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	        try {
				message.setSubject(email.getSubject());
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	        try {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email.getBcc()));
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	 
	        try {
				transport.connect();
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	        try {
				transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	        try {
				transport.close();
			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}
	        return true;
		} else {
			logger.info(email.toString());
			return false;
		}
	}
	
	public boolean isLogOnlyDontSend() {
		return logOnlyDontSend;
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
	           String username = SMTP_AUTH_USER;
	           String password = SMTP_AUTH_PWD;
	           return new PasswordAuthentication(username, password);
	    }
	 }

}
