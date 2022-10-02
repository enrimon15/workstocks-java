package it.workstocks.exception.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import it.workstocks.presentation.Routes;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@Value(value = "${error.show}")
	private String showError = "false";

	@ExceptionHandler({Exception.class, RuntimeException.class})
	public String handleException(HttpServletRequest request, Exception ex, Model model) {
		log.error("Exception Occured:: URL=" + request.getRequestURL() + ", method=" + request.getMethod(), ex);

		if (Boolean.parseBoolean(showError)) {
			String cause = (ex.getCause() == null) ? "" : ex.getCause().getMessage();
			model.addAttribute("errorCause", cause);
			model.addAttribute("errorMessage", ex.getMessage());
		}
		
		return "redirect:" + Routes.ERROR;
	}

}
