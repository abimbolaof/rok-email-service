package com.sonofiroko.email.model;

import java.util.Map;

/**
 * Created By: Olusegun Abimbola Sep 3, 2017
 **/
public class EmailEvent {
	protected String from;
	protected String to;
	protected String body;
	protected String subject;
	private String templateName;
	private Map<String, String> values;

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

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Map<String, String> getValues() {
		return values;
	}

	public void setValues(Map<String, String> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "AbstractMessage{" +
				"from='" + from + '\'' +
				", to='" + to + '\'' +
				", body='" + body + '\'' +
				", subject='" + subject + '\'' +
				", messageTemplateType=" + templateName +
				'}';
	}
}
