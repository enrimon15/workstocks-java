package it.workstocks.utils;

import org.apache.commons.validator.routines.UrlValidator;

public class StringUtils {
	private StringUtils() {
		throw new UnsupportedOperationException(
				String.format("The class %s should be accessed in a static way!", this.getClass().getName()));
	}

	public static boolean isEmpty(String s) {
		return org.apache.commons.lang3.StringUtils.isEmpty(s);
	}

	public static boolean isNotEmpty(String s) {
		return org.apache.commons.lang3.StringUtils.isNotEmpty(s);
	}

	public static boolean isBlank(String s) {
		return org.apache.commons.lang3.StringUtils.isBlank(s);
	}

	public static boolean isNotBlank(String s) {
		return org.apache.commons.lang3.StringUtils.isNotBlank(s);
		
	}
	
	public static boolean isValidUrl(String s) {
		UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"});
		return urlValidator.isValid(s);	
	}

}
