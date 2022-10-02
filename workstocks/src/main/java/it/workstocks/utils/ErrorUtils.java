package it.workstocks.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.Errors;

import it.workstocks.dto.error.ErrorValidationModel;

public class ErrorUtils {
	
	private ErrorUtils() {
		throw new UnsupportedOperationException(
				String.format("The class %s should be accessed in a static way!", this.getClass().getName()));
	}
	
	public static List<ErrorValidationModel> getErrorList(Errors errors) {
		List<ErrorValidationModel> errorMessages = errors.getFieldErrors()
				.stream()
	            .map(err -> new ErrorValidationModel(err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
	            .distinct()
	            .collect(Collectors.toList());
		
		return errorMessages;
	}
	
	public final static String WRONG_PAYLOAD = "error.wrongPayload";
	public final static String APPLICATION_EXPIRED = "error.expiredApplication";
	public final static String NULL_TOKEN = "error.nullToken";
	public final static String INVALID_TOKEN = "error.invalidToken"; 
	public final static String TOKEN_EXPIRED = "error.expiredToken"; 
	public final static String TOKEN_NOT_FOUND = "error.notFoundToken"; 
	public final static String PHOTO_NOT_FOUND = "error.photoNotFound";
	public final static String PHOTO_REQUIRED = "error.requiredPhoto";
	public final static String FILE_TOO_LARGE = "error.fileTooLarge";
	public final static String CV_NOT_FOUND = "error.curruculumNotFound"; 
	public final static String NOT_FOUND = "error.genericNotFound"; 
	public final static String JOB_ALREADY_APPLIED = "error.jobAlreadyApplied"; 
	public final static String USER_EMAIL_NOT_FOUND = "error.userEmailNotFound"; 
	public final static String EMAIL = "error.email"; 
	public final static String FAVOURITE_ALREADY_APPLIED = "favouriteAlreadyAdded"; 
	public final static String FAVOURITE_NOT_FOUND = "error.favouriteNotFound"; 
	public final static String JOB_ALERT_ALREADY_APPLIED = "jobAlertAlreadyAdded"; 
	public final static String JOB_ALERT_NOT_FOUND = "error.jobAlertNotFound"; 
	public final static String LIKE_ALREADY_SUBMIT = "likeAlreadySubmit"; 
	public final static String LIKE_NOT_FOUND = "error.likeNotFound"; 
	public final static String COMMENT_NOT_FOUND = "error.commentNotFound"; 
	public final static String REVIEW_NOT_FOUND = "error.reviewNotFound"; 
	public static final String INVALID_NUMBER_MAX = "requestParam.numberMax";
	public static final String INVALID_NUMBER_MIN = "requestParam.numberMin";
	public final static String INVALID_REQUEST_PARAM = "error.requestParam";
	public final static String INVALID_LOGIN_CREDENTIALS = "error.invalidLoginCredentials";
	public final static String GENERIC_ERROR = "error.generic";
	public final static String USER_UNAUTHORIZED = "error.forbidden";
	public final static String TOKEN_FORBIDDEN = "error.jwtInvalid";

}
