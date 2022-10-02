package it.workstocks.utils;

import it.workstocks.presentation.Templates;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MailType {
	CONTACT(Templates.EMAIL_CONTACT),
	APPLICATION_APPLICANT(Templates.EMAIL_APPLICATION_APPLICANT),
	APPLICATION_COMPANY(Templates.EMAIL_APPLICATION_COMPANY),
	RESET_PASSWORD(Templates.EMAIL_RESET_PASSWORD),
	JOB_ALERT(Templates.EMAIL_JOB_ALERT);
	
	private Templates template;
	
	
}
