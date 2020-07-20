package com.sonofiroko.email.service;

import com.sonofiroko.email.model.ApiException;
import com.sonofiroko.email.model.Message;
import com.sonofiroko.email.types.MessageTemplateType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By: Olusegun Abimbola Dec 15, 2017
 **/
public class MessageTemplateProvider<K extends Message> {

	final private static Map<MessageTemplateType, String> templateMap = new HashMap<>();
	
	static {
		loadTemplates();
	}
	
	private static void loadTemplates(){
		// Load templates from file system
				String[] typeNames = MessageTemplateType.getAllNames();
				ClassLoader classLoader = MessageTemplateProvider.class.getClassLoader();
				String folder = "templates";
				String extension = ".html";

				for (int i = 0; i < typeNames.length; i++) {
					String filename = folder + "/" + typeNames[i].toLowerCase() + extension;
					URL url = classLoader.getResource(filename);
					if (url != null) {
						String content = readFileContents(url.getFile());
						MessageTemplateType type = MessageTemplateType.fromName(typeNames[i]);
						templateMap.put(type, content);
					}
				}
	}
	
	private static String readFileContents(String file){
		StringBuilder content = new StringBuilder();
		try (FileReader reader = new FileReader(file);
				BufferedReader br = new BufferedReader(reader)) {
			String s = "";
			while ((s = br.readLine()) != null) {
				content.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(content.toString().getBytes(), StandardCharsets.UTF_8);
	}

	private Class<?> messageClass;
	private Map<String, String> values = new HashMap<>();
	private String templateBody;

	private MessageTemplateProvider() {
	}

	public static <L extends Message> MessageTemplateProvider<L> newInstance() {
		return new MessageTemplateProvider<L>();
	}

	public MessageTemplateProvider<K> setValue(String key, String value) {
		this.values.put(key, value);
		return this;
	}

	public void apply(K message) throws ApiException {
		if (values == null)
			throw new ApiException("Template values are null.");

		templateBody = templateMap.get(message.getTemplateType());
		// build message body by substituting the place-holders in the template
		// body with the supplied values
		if (templateBody != null) {
			String token;
			for (String key : values.keySet()) {
				token = "[[" + key + "]]";
				templateBody = templateBody.replace(token, values.get(key));	
			}
			message.setBody(templateBody);
		}
	}

	public Class<?> getMessageClass() {
		return messageClass;
	}

	public Map<String, String> getValues() {
		return values;
	}

	public MessageTemplateProvider<K> setValues(Map<String, String> values) {
		this.values = values;
		return this;
	}

	public void setTemplateBody(String templateBody) {
		this.templateBody = templateBody;
	}

	public static Map<MessageTemplateType, String> getTemplatemap() {
		return templateMap;
	}
}
