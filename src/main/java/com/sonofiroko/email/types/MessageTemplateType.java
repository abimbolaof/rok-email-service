package com.sonofiroko.email.types;

/**
 * Created By: Olusegun Abimbola Dec 15, 2017
 **/

/**
 * NOTE: Enum values must correspond exactly with the message template file names
 */

public enum MessageTemplateType {
	Purchase("Purchase"),
	Registration("Registration"),
	Contact("Contact"),
	Reset("Reset Password"),
	Subscription_Expiry("Subscription Expiration"),
	Subscription_Cancellation("Subscription Cancellation");

	private static String[] allNames;

	private String title;

	static {
		allNames = convertAllNames();
	}

    MessageTemplateType(String title) {
	    this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static String[] getAllNames() {
		return allNames;
	}

	private static String[] convertAllNames() {
		MessageTemplateType[] values = values();
		String[] retVal = new String[values.length];
		int i = 0;
		for (MessageTemplateType type : values) {
			retVal[i] = type.name();
			i++;
		}
		return retVal;
	}

	public static MessageTemplateType fromName(String name) {
		MessageTemplateType[] values = values();
		for (MessageTemplateType type : values) {
			if (name.toLowerCase().equals(type.name().toLowerCase()))
				return type;
		}
		return null;
	}
}
