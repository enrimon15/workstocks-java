package it.workstocks.security;

public class Roles {
	private static final String ROLE_PREFIX = "ROLE_";
	public static final String COMPANY_OWNER = ROLE_PREFIX + "COMPANY_OWNER";
	public static final String APPLICANT = ROLE_PREFIX + "APPLICANT";
	public static final String ADMIN = ROLE_PREFIX +"ADMIN";
	public static final String ANONYMOUS = ROLE_PREFIX +"ANONYMOUS";
	
	public Roles() {
		throw new UnsupportedOperationException(String.format("The class: %s should be accessed in a static way!", Roles.class));
	}
}
