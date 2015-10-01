package com.mayloom.vt.util;

public final class BasicEmail {

	private String to; 
	private final String from; 
	private final String subject;
	private final String bodyPlain; 
	private final String bodyHtml;
	private String bcc;
	
	public BasicEmail(String from, String subject, String bodyPlain, String bodyHtml) {
		this.from = from;
		this.subject = subject;
		this.bodyPlain = bodyPlain;
		this.bodyHtml = bodyHtml;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public String getTo() {
		return to;
	}

	public String getFrom() {
		return from;
	}

	public String getSubject() {
		return subject;
	}

	public String getBodyPlain() {
		return bodyPlain;
	}

	public String getBodyHtml() {
		return bodyHtml;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("to: ");
		builder.append(to);
		builder.append(" from: ");
		builder.append(from);
		builder.append(" subject: ");
		builder.append(subject);
		builder.append(" plain body: ");
		builder.append(bodyPlain);
		builder.append(" html body: ");
		builder.append(bodyHtml);
		return builder.toString();
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
}
