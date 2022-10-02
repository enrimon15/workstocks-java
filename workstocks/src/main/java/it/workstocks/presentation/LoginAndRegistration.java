package it.workstocks.presentation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.registration.Registration;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.service.UserService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.validator.RegistrationFormValidator;

@Controller
public class LoginAndRegistration {
	
	@Autowired
	private WorkstocksProperties props;

	@Autowired
	private UserService userService;

	@GetMapping(Routes.LOGIN)
	public String loginPage() {
		Authentication authentication = AuthUtility.getAuth();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return Templates.LOGIN_PAGE.getTemplate();
		}

		return "redirect:" + Routes.HOME_PAGE;
	}

	@GetMapping(Routes.REGISTER)
	public String registrationStart(Model model) {
		model.addAttribute("registrationForm", new Registration());
		return Templates.REGISTRATION_PAGE.getTemplate();
	}

	@PostMapping(Routes.REGISTER)
	public String registration(@Valid @ModelAttribute("registrationForm") Registration registrationBody, Errors errors,
			HttpServletRequest request) throws WorkstocksBusinessException {
		RegistrationFormValidator validator = new RegistrationFormValidator();
		validator.validate(registrationBody, errors);

		if (errors.hasErrors()) {
			return Templates.REGISTRATION_PAGE.getTemplate();
		}

		userService.save(registrationBody);

		try {
			request.login(registrationBody.getEmail(), registrationBody.getPassword());
		} catch (ServletException e) {
			throw new WorkstocksBusinessException("registration: Error while login post registration: " + e.getMessage());
		}

		return "redirect:" + Routes.HOME_PAGE;
	}

	@PostMapping(Routes.ADMIN_REGISTER)
	@ResponseBody
	public ResponseEntity<String> adminRegistration(@Valid @RequestBody Registration registrationBody, Errors errors,
			@RequestHeader("api-key") String apiKey) throws WorkstocksBusinessException {

		if (!props.getAdminApiKey().equals(apiKey)) {
			return new ResponseEntity<String>("Utente non autorizzato, api-key non valida", HttpStatus.UNAUTHORIZED);
		}

		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body("Utente non valido, ricontrollare che i campi siano validi!");
		}

		userService.save(registrationBody);

		return ResponseEntity.ok("Utente: " + registrationBody.getEmail() + " registrato con successo!");
	}
}
