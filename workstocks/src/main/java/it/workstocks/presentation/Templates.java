package it.workstocks.presentation;

public enum Templates {
	/*
	 * TECHNICAL TEMPLATES
	 */
	ERROR_PAGE("error/error"),
	LOGIN_PAGE("auth/login"),
	REGISTRATION_PAGE("auth/register"),
	DENIED("/denied"),
	INDEX("index"),
	RESET_PASSWORD_REQUEST("auth/passwords/reset-password-request"),
	RESET_PASSWORD("auth/passwords/reset-password"),
	/*
	 * APPLICANT TEMPLATES
	 */
	APPLICANT_PROFILE("applicant/dashboard/applicant-dashboard"),
	APPLICANT_PUBLIC_PROFILE("applicant/applicant-details"),
	APPLICANTS("applicant/applicant-search"),
	APPLICANTS_DATA("applicant/applicant-search-data"),
	CV_TEMPLATE("applicant/dashboard/cv-pdf"),
	
	/*
	 * JOB OFFER TEMPLATES
	 */
	JOB_OFFERS("jobs/job-search"),
	JOB_OFFERS_DATA("jobs/job-search-data"),
	JOB_OFFER_DETAILS("jobs/job-details"),
	
	/* 
	 * COMPANY TEMPLATES
	 */
	COMPANY_OWNER_PROFILE("company/dashboard/company-dashboard"),
	COMPANY_PUBLIC_PROFILE("company/company-details"),
	COMPANIES("company/company-search"),
	COMPANIES_DATA("company/company-search-data"),
	
	/*
	 * NEWS
	 */
	ADMIN_NEWS("admin/news"),
	ADMIN_NEWS_DETAILS("admin/news-details"),
	/*
	 * BLOG
	 */
	BLOG("blog/blog"),
	BLOG_DATA("blog/blog-data"),
	BLOG_DETAILS("blog/blog-details"),
	BLOG_NEWS_COMMENTS("blog/comment-data"),
	/*
	 * ADMIN
	 */
	ADMIN_DASHBOARD("admin/admin-dashboard"),
	/*
	 * EMAIL TEMPLTATES
	 */
	EMAIL_CONTACT("emails/mail-contact"),
	EMAIL_APPLICATION_APPLICANT("emails/mail-application-toSubscriber"),
	EMAIL_APPLICATION_COMPANY("emails/mail-application-toCompany"),
	EMAIL_RESET_PASSWORD("emails/mail-reset"),
	EMAIL_JOB_ALERT("emails/mail-job-alert")

	;
	
	private String template;

	private Templates(String template) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}
	
	
	

}
