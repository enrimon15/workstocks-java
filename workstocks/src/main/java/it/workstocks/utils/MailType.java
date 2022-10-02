package it.workstocks.utils;
import lombok.Getter;

@Getter
public enum MailType {
	CONTACT("emails/mail-contact"),
	APPLICATION_APPLICANT("emails/mail-application-toSubscriber"),
	APPLICATION_COMPANY("emails/mail-application-toCompany"),
	RESET_PASSWORD("emails/mail-reset"),
	JOB_ALERT("emails/mail-job-alert");
	
	private String template;
	
	MailType(String template) {
		this.template = template;
	}
	
	public String getTemplate() {
		return this.template;
	}
}
