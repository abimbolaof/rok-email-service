package com.sonofiroko.email.service;

import com.sonofiroko.email.model.ApiException;
import com.sonofiroko.email.model.EmailEvent;
import com.sonofiroko.email.types.MessageTemplateType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created By: Olusegun Abimbola Dec 15, 2017
 **/
public class MessageTemplateProvider<K extends EmailEvent> {

	final private static Map<MessageTemplateType, String> templateMap = new HashMap<>();
	
	private static void loadTemplates(){
		// Load templates from file system
				String[] typeNames = MessageTemplateType.getAllNames();
				ClassLoader classLoader = MessageTemplateProvider.class.getClassLoader();
				String folder = "templates";
				String extension = ".html";

				for (int i = 0; i < typeNames.length; i++) {
					String filename = folder + "/" + typeNames[i].toLowerCase() + extension;
					InputStream is = classLoader.getResourceAsStream(filename);
					String content = readFileContents(is);
					MessageTemplateType type = MessageTemplateType.fromName(typeNames[i]);
					templateMap.put(type, content);
				}
	}

	private static String readFileContents(InputStream is){
		StringBuilder content = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String s = "";
			while ((s = br.readLine()) != null) {
				content.append(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(content.toString().getBytes(), StandardCharsets.UTF_8);
	}
	
//	private static String readFileContents(String file){
//		StringBuilder content = new StringBuilder();
//		try (FileReader reader = new FileReader(file);
//				BufferedReader br = new BufferedReader(reader)) {
//			String s = "";
//			while ((s = br.readLine()) != null) {
//				content.append(s);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return new String(content.toString().getBytes(), StandardCharsets.UTF_8);
//	}

	private Map<String, String> parameters = new HashMap<>();
	private String templateBody;

	private MessageTemplateProvider() {
		if (templateMap.size() == 0){
			loadTemplates();
		}
	}

	public static <L extends EmailEvent> MessageTemplateProvider<L> newInstance() {
		return new MessageTemplateProvider<L>();
	}

	public MessageTemplateProvider<K> setParameter(String key, String value) {
		this.parameters.put(key, value);
		return this;
	}

	public void apply(K message, MessageTemplateType templateType) throws ApiException {
		if (parameters == null)
			throw new ApiException("Template parameters are null.");

		templateBody = templateMap.get(templateType);
		// build message body by substituting the place-holders in the template
		// body with the supplied parameters
		if (templateBody != null) {
			String token;
			for (String key : parameters.keySet()) {
				token = "[[" + key + "]]";
				templateBody = templateBody.replace(token, parameters.get(key));
			}
			message.setBody(templateBody);
		}
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public MessageTemplateProvider<K> setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
		return this;
	}

	public void setTemplateBody(String templateBody) {
		this.templateBody = templateBody;
	}

	public static Map<MessageTemplateType, String> getTemplatemap() {
		return templateMap;
	}
}
