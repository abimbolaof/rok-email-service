package com.sonofiroko.email.model;

import com.sonofiroko.email.types.MessageFormat;
import com.sonofiroko.email.types.MessageTemplateType;

/**
 * Created By: Olusegun Abimbola Sep 3, 2017
 **/
public class Message {

	protected String from;
	protected String to;
	protected String body;
	protected String subject;
	private MessageTemplateType templateType;

	protected MessageFormat messageFormat = MessageFormat.EMAIL;

	public Message(){}

	public Message(String from, String to, MessageTemplateType templateType) {
		this.from = from;
		this.to = to;
		this.templateType = templateType;
		this.subject = templateType.getTitle();
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public MessageTemplateType getTemplateType() {
		return templateType;
	}

	public void setTemplateType(MessageTemplateType templateType) {
		this.templateType = templateType;
	}

	public MessageFormat getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(MessageFormat messageFormat) {
		this.messageFormat = messageFormat;
	}

	@Override
	public String toString() {
		return "AbstractMessage{" +
				"from='" + from + '\'' +
				", to='" + to + '\'' +
				", body='" + body + '\'' +
				", subject='" + subject + '\'' +
				", messageTemplateType=" + templateType +
				", messageFormat=" + messageFormat +
				'}';
	}
}
