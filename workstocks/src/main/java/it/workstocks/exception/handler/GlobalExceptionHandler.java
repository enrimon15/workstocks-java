package it.workstocks.exception.handler;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.jsonwebtoken.JwtException;
import it.workstocks.dto.error.ErrorResponse;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@Autowired
	private Translator translator;
	
	@ExceptionHandler(AuthenticationException.class)
	public void handleAutenticationException(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex) throws IOException {
		log.error("Autentication Exception:: URL=" + request.getRequestURL() + ", method=" + request.getMethod(), ex);
		response.sendError(HttpStatus.UNAUTHORIZED.value(), translator.toLocale(ErrorUtils.INVALID_LOGIN_CREDENTIALS, null));
	}
	
	@ExceptionHandler(JwtException.class)
	public void handleJWTAutenticationException(HttpServletRequest request, HttpServletResponse response, JwtException ex) throws IOException {
		log.error("JWT Autentication Exception:: URL=" + request.getRequestURL() + ", method=" + request.getMethod(), ex);	
		response.sendError(HttpStatus.UNAUTHORIZED.value(), translator.toLocale(ErrorUtils.TOKEN_FORBIDDEN, null));
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public void handleAccessDeniedException(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) throws IOException {
		log.error("Access Denied:: URL=" + request.getRequestURL() + ", method=" + request.getMethod(), ex);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			log.error("User: " + auth.getName() + " attempted to access the protected URL: " + request.getRequestURI());
		}
		
		response.sendError(HttpStatus.FORBIDDEN.value(), translator.toLocale(ErrorUtils.USER_UNAUTHORIZED, null));
	}
	
	@ExceptionHandler(WorkstocksBusinessException.class)
	public ResponseEntity<ErrorResponse> handleWorkstockException(HttpServletRequest request, HttpServletResponse response, WorkstocksBusinessException ex) throws IOException {	
		log.error(String.format("Workstocks Exception:: URL=%s, method=%s, status=%s", request.getRequestURL(), request.getMethod(), ex.getStatus()), ex);
		
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatus(ex.getStatus().value());
		errorResponse.setTimestamp(LocalDateTime.now());
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setPath(request.getRequestURI());
		errorResponse.setError(ex.getStatus().getReasonPhrase());
		
		if (ex.getErrors() != null && HttpStatus.BAD_REQUEST.equals(ex.getStatus())) {
			errorResponse.setValidationErrors(ex.getErrors());		
		} 

		return ResponseEntity.status(ex.getStatus()).body(errorResponse);
		
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public void handleRequiredParameterException(HttpServletRequest request, HttpServletResponse response, MissingServletRequestParameterException ex) throws IOException {
		log.error("Exception Occured:: URL=" + request.getRequestURL() + ", method=" + request.getMethod(), ex);
		response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
	}

	@ExceptionHandler({Exception.class, RuntimeException.class})
	public void handleGenericException(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
		log.error("Exception Occured:: URL=" + request.getRequestURL() + ", method=" + request.getMethod(), ex);
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), translator.toLocale(ErrorUtils.GENERIC_ERROR, null));
	}

}
