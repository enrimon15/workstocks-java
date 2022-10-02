package it.workstocks.rest.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.auth.AuthenticationRequest;
import it.workstocks.dto.auth.PasswordDto;
import it.workstocks.dto.auth.PasswordResetRequest;
import it.workstocks.dto.auth.RegistrationRequest;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.rest.AuthV1;
import it.workstocks.security.UserDetailsImpl;
import it.workstocks.security.jwt.JWTUtil;
import it.workstocks.service.AuthService;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.FileUtils;
import it.workstocks.utils.StringUtils;
import it.workstocks.utils.Translator;
import it.workstocks.validator.PasswordValidator;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AuthV1Impl implements AuthV1 {
	
	@Autowired
    private AuthenticationManager authenticationManager;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private WorkstocksProperties props;
	
	@Autowired
    private JWTUtil jwtTokenUtil;
	
	@Value("${jwt.token.header}")
    private String tokenHeader;
	
    @Value("${jwt.token.prefix}")
    private String tokenPrefix;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Autowired
	private Translator translator;
    
    @Autowired
    private PasswordValidator passwordValidator;

	@Override
	public ResponseEntity<?> login(AuthenticationRequest authenticationRequest, Errors errors) throws WorkstocksBusinessException {
    	if (errors.hasErrors()) {
    		throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"authentication request"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
    	}
    	
        // Effettuo l'autenticazione
        Authentication authentication = authenticationManager.authenticate(
        		new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword())
        );

        // Genero il Token e lo inserisco nell'header di risposta
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);
        log.debug(String.format("Token %s", token));

        Map<String, Object> user = new HashMap<>();
        user.put("id", userDetails.getUser().getId());
        user.put("name", userDetails.getUser().getName());
        user.put("surname", userDetails.getUser().getSurname());
        user.put("email", userDetails.getUser().getEmail());
        user.put("jobTitle", userDetails.getUser().getJobTitle());
        user.put("photo", userDetails.getUser().getAvatar() != null ? FileUtils.getBase64FromByteArray(userDetails.getUser().getAvatar()) : null);
        user.put("token", token);
        user.put("expirationMillis", expiration);
        
        return ResponseEntity.ok().header(tokenHeader, tokenPrefix + " " + token).body(user);
	}

	@Override
	public ResponseEntity<Void> logout(String auth) {
		String token = auth.substring(tokenPrefix.length() + 1);
		jwtTokenUtil.invalidateToken(token);
		return ResponseEntity.noContent().build();
	}

	@SuppressWarnings("static-access")
	@Override
	public ResponseEntity<Void> registration(@Valid RegistrationRequest registrationRequest, Errors errors, HttpServletRequest request, UriComponentsBuilder uriBuilder) throws WorkstocksBusinessException {
		PasswordDto passwordDto = new PasswordDto(registrationRequest.getPassword(), registrationRequest.getPasswordConfirmation());
		passwordValidator.validate(passwordDto, errors);
		
		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"registration request"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}

		Long applicantCreatedId = authService.save(registrationRequest);

		try {
			request.login(registrationRequest.getEmail(), registrationRequest.getPassword());
		} catch (ServletException e) {
			log.error("Error while login post registration", e);
			throw new WorkstocksBusinessException("Error while login post registration", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseEntity.created(uriBuilder
				.fromPath(props.getSite().getUrl() + "/v1/applicants/{applicantId}")
				.buildAndExpand(applicantCreatedId)
				.toUri())
				.build();
	}

	@Override
	public ResponseEntity<Void> passwordResetRequest(PasswordResetRequest resetRequest, Errors errors)
			throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"reset password"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}
		
		String token = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
	    authService.createPasswordResetTokenForUser(resetRequest.getEmail(), token);
	    
	    return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<Void> resetPassword(@Valid PasswordDto passwordDto, Errors errors)
			throws WorkstocksBusinessException {
		passwordValidator.validate(passwordDto, errors);
		
		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"reset password"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}
		
		if (StringUtils.isBlank(passwordDto.getToken())) {
			errors.rejectValue("token", translator.toLocale(ErrorUtils.NULL_TOKEN, null));
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"reset password"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}
		
		authService.validatePasswordResetToken(passwordDto.getToken());
		authService.updatePasswordByToken(passwordDto);
		return ResponseEntity.noContent().build();
	}

}
