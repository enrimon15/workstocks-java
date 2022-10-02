package it.workstocks.presentation;

public  class Routes {
	/*
	 * FAMILY ROUTES
	 */
	public static final String ROOT_PUBLIC = "/public";
	public static final String ROOT_APPLICANT = "/applicant";
	public static final String ROOT_COMPANY = "/company";
	public static final String ROOT_COMMON = "/common";
	public static final String ROOT_RESET_PASSWORD = "/reset-password";
	public static final String ROOT_ONLINE_CV = ROOT_APPLICANT + "/online-cv";
	public static final String ROOT_HANDLE_OFFERS = ROOT_COMPANY + "/handle-offers";
	public static final String ROOT_WORKING_PLACES = ROOT_COMPANY + "/working-places";
	public static final String ROOT_ADMIN = "/admin";
	public static final String ROOT_REVIEW = "applicant/review";
	/*
	 * TECHNICAL ROUTES
	 */
	public static final String DENIED = "/denied";
	public static final String CHANGE_PASSWORD = "/change-password";
	public static final String SEND_EMAIL = "/send-mail";
	public static final String LOGIN = "/login";
	public static final String REGISTER = "/register";
	public static final String ADMIN_REGISTER = "/admin-register";
	public static final String PASSWORD_RESET_REQUEST = "/request";
	public static final String PASSWORD_CHANGE_PASSWORD = "/changePassword";
	public static final String EDIT = "/edit";
	public static final String DELETE = "/delete";
	public static final String ERROR = "/error";
	/*
	 * HOME PAGE
	 */
	public static final String HOME_PAGE = "/";
	/*
	 * JOB OFFERS ROUTES
	 */
	public static final String JOB_OFFER = "/job-offer";
	public static final String JOB_OFFER_FAVORITE = JOB_OFFER + "/favorite";
	public static final String JOB_OFFER_APPLY = JOB_OFFER + "/apply";
	public static final String JOB_OFFERS = "/job-offers";
	public static final String JOB_OFFER_DETAIL_PAGE = ROOT_PUBLIC + JOB_OFFER;
	public static final String JOB_ALERT="/job-alert";
	/*
	 * NEWS ADMIN
	 */
	public static final String NEWS = "/news";
	public static final String NEWS_LIST = "/news-list";
	public static final String NEWS_DELETE = NEWS + "/delete";
	/*
	 * BLOG
	 */
	public static final String BLOG = "/blog";
	public static final String BLOG_LIKE = BLOG+"/like";
	public static final String BLOG_COMMENT = BLOG+"/comment";
	public static final String BLOG_COMMENT_DELETE = BLOG_COMMENT+"/delete";
	public static final String BLOG_NEWS = BLOG+"/news";
	public static final String BLOG_NEWS_DATA = BLOG+"/news-data";
	public static final String BLOG_NEWS_COMMENTS = BLOG+"/news-detail/comments-data";
	/*
	 * PUBLIC PROFILES
	 */
	public static final String APPLICANT_PUBLIC_PAGE = "/applicant";
	public static final String COMPANY_PUBLIC_PAGE = "/company";
	public static final String COMPANIES = "/companies";
	public static final String APPLICANTS = "/applicants";
	/*
	 * APPLICANT ONLINE CV
	 */
	public static final String QUALIFICATION = "/qualification";
	public static final String EXPERIENCE = "/experience";
	public static final String CERTIFICATE = "/certificate";
	public static final String SKILL = "/skill";
	public static final String CV = "/cv";
	public static final String EDIT_QUALIFICATION = EDIT + "/qualification";
	public static final String EDIT_EXPERIENCE = EDIT + "/experience";
	public static final String EDIT_CERTIFICATE = EDIT + "/certificate";
	public static final String EDIT_SKILL = EDIT + "/skill";

	/*
	 * COMMON PROFILE
	 */
	public static final String PROFILE = "/profile";
	/*
	 * APPLCIANT PROFILE
	 */
	public static final String DOWNLOAD_CV = "/download-cv";
	public static final String FAVORITES = "/favorites";
	public static final String FAVORITES_DELETE = FAVORITES + "-delete";
	public static final String APPLIED_JOBS = "/applied-jobs";
	public static final String APPLIED_JOBS_DELETE = APPLIED_JOBS + "-delete";
	/*
	 * COMPANY PROFILE
	 */
	public static final String NEW_OFFER = "/new-offer";
	public static final String CANDIDATES = "/candidates";
	
	
	private Routes() {
		throw new UnsupportedOperationException("Access this class in a stiatic way!");
	}

}
