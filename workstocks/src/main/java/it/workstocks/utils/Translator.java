package it.workstocks.utils;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class Translator {
	
	@Autowired
	private ResourceBundleMessageSource messageSource;
	
	public String toLocale(String msgCode, Object[] args) {
	      Locale locale = LocaleContextHolder.getLocale();
	      return messageSource.getMessage(msgCode, args != null ? args : null, locale);
	}
	

}
