package it.workstocks.rest;

import static it.workstocks.utils.AuthUtility.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.workstocks.dto.auth.AuthenticationRequest;
import it.workstocks.dto.auth.PasswordDto;
import it.workstocks.dto.auth.PasswordResetRequest;
import it.workstocks.dto.auth.RegistrationRequest;
import it.workstocks.exception.WorkstocksBusinessException;

@RequestMapping(path = "/v1/auth", produces = "application/json")
public interface AuthV1 {
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "AUTH")
	@Operation(summary = "User sign-in (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "User succesfully sign-in"),
		@ApiResponse(responseCode = "400", description = "Wrong user payload", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@PostMapping(path = "login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationRequest authenticationRequest, Errors errors) throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "AUTH")
	@Operation(summary = "User logout (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "User succesfully logout"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@DeleteMapping("logout")
	public ResponseEntity<Void> logout(@RequestHeader("Authorization") String auth);
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "AUTH")
	@Operation(summary = "User sign-up (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "User succesfully signup"),
		@ApiResponse(responseCode = "400", description = "Wrong user payload"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "register", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> registration(@RequestBody @Valid RegistrationRequest registrationRequest, Errors errors, HttpServletRequest request, UriComponentsBuilder uriBuilder) throws WorkstocksBusinessException;
	
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "AUTH")
	@Operation(summary = "Password reset request (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Wrong password reset payload"),
		@ApiResponse(responseCode = "404", description = "User not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "reset-password-request", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> passwordResetRequest(@RequestBody @Valid PasswordResetRequest resetRequest, Errors errors) throws WorkstocksBusinessException;
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "AUTH")
	@Operation(summary = "Reset password (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Wrong password payload"),
		@ApiResponse(responseCode = "404", description = "Token not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Token not valid"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PutMapping(path = "reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> resetPassword(@RequestBody @Valid PasswordDto passwordDto, Errors errors) throws WorkstocksBusinessException;
}
