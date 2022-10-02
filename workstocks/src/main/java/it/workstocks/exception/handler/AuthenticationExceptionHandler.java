package it.workstocks.exception.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AuthenticationExceptionHandler implements AuthenticationEntryPoint {
	
	@Autowired
	private Translator translator;

	// Questo metodo e' invocato quando un utente tenta di accedere ad un endpoint non pubblico senza credenziali corrette o con un token non esatto
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
    	log.error("Invalid login credentials: " + authException);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, translator.toLocale(ErrorUtils.INVALID_LOGIN_CREDENTIALS, null));
    }
}
