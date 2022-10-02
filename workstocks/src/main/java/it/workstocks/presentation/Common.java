package it.workstocks.presentation;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.workstocks.dto.user.PasswordDto;
import it.workstocks.dto.user.UserDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.security.Roles;
import it.workstocks.service.EmailService;
import it.workstocks.service.UserService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.validator.PasswordValidator;

@Controller
@RequestMapping(Routes.ROOT_COMMON)
public class Common {

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	private String getDashboardByRole() throws WorkstocksBusinessException {
		if (AuthUtility.hasRole(Roles.COMPANY_OWNER)) {
			return Templates.COMPANY_OWNER_PROFILE.getTemplate();
		} else if (AuthUtility.hasRole(Roles.APPLICANT)) {
			return Templates.APPLICANT_PROFILE.getTemplate();
		} else if (AuthUtility.hasRole(Roles.ADMIN)){
			return Templates.ADMIN_DASHBOARD.getTemplate();
		} else {
			throw new WorkstocksBusinessException("Role not found!");
		}
	}

	@GetMapping(Routes.CHANGE_PASSWORD)
	public String changePwPage(Model model) throws WorkstocksBusinessException {
		model.addAttribute("changePw", new PasswordDto());
		return getDashboardByRole();
	}

	@PostMapping(Routes.CHANGE_PASSWORD)
	public String changePwExecute(@Valid @ModelAttribute("changePw") PasswordDto changePasswordDto, Errors errors,
			RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		PasswordValidator validator = new PasswordValidator();
		validator.validate(changePasswordDto, errors);

		if (errors.hasErrors()) {
			return getDashboardByRole();
		}

		userService.updatePassword(changePasswordDto, AuthUtility.getCurrentUser().getId());
		redirectAttributes.addFlashAttribute("successMsg", "changePassword.success");

		return "redirect:" + Routes.ROOT_COMMON + Routes.CHANGE_PASSWORD;
	}

	@PostMapping(Routes.SEND_EMAIL)
	public String sendProfileMail(@RequestParam Long recipient, @RequestParam String sourceTextMessage,
			@RequestHeader String referer, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		UserDto userRecipient = userService.findUserById(recipient);

		try {
			emailService.sendContactRequest(userRecipient.getEmail(), AuthUtility.getCurrentUser().getEmail(), sourceTextMessage);
		} catch (WorkstocksBusinessException e) {
			redirectAttributes.addFlashAttribute("msgError", "up.mailError");
		}

		redirectAttributes.addFlashAttribute("msgSuccess", "up.mailSuccess");
		return "redirect:" + referer;
	}

}
