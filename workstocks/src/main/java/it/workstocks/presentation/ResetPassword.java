package it.workstocks.presentation;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.workstocks.dto.user.PasswordDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.service.EmailService;
import it.workstocks.service.UserService;
import it.workstocks.utils.TokenValid;
import it.workstocks.validator.PasswordValidator;

@Controller
@RequestMapping(Routes.ROOT_RESET_PASSWORD)
public class ResetPassword {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EmailService emailService;
	
	@GetMapping
	public String getRequestPassword(Model model) {
		return Templates.RESET_PASSWORD_REQUEST.getTemplate();
	}
	
	private String handleErrorResetPasswordRequest(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("msgError", "loginAndRegister.resetRequest.msgError");
	    return "redirect:" + Routes.ROOT_RESET_PASSWORD;
	}
	
	@PostMapping(Routes.PASSWORD_RESET_REQUEST)
	public String process(@RequestParam String email, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
	    String token = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
	    boolean result = userService.createPasswordResetTokenForUser(email, token);
	    if (!result) return handleErrorResetPasswordRequest(redirectAttributes);

	    //send mail
	    emailService.sendResetToken(email, token);
	    redirectAttributes.addFlashAttribute("msgSuccess", "loginAndRegister.resetRequest.msgSuccess");
	    return "redirect:"+Routes.ROOT_RESET_PASSWORD;
	}
	
	@GetMapping(Routes.PASSWORD_CHANGE_PASSWORD)
	public String showChangePasswordReset(@RequestParam("token") String token, Model model) throws WorkstocksBusinessException {
		PasswordDto pwDto = new PasswordDto();
		pwDto.setToken(token);
        model.addAttribute("resetPw", pwDto);
        return Templates.RESET_PASSWORD.getTemplate();
	}
	
	@PostMapping(Routes.PASSWORD_CHANGE_PASSWORD)
	public String changePasswordReset(@Valid @ModelAttribute("resetPw") PasswordDto resetPasswordDto, Errors errors, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		PasswordValidator validator = new PasswordValidator();
		validator.validate(resetPasswordDto, errors);
		
		if (errors.hasErrors()) {
			return Templates.RESET_PASSWORD.getTemplate();
		}
		
		TokenValid tokenValid = userService.validatePasswordResetToken(resetPasswordDto.getToken());
		
		switch (tokenValid) {
		case EXPIRED:
			redirectAttributes.addFlashAttribute("msgError", "loginAndRegister.reset.msgErrorExpired");
			break;
		case INVALID:
			redirectAttributes.addFlashAttribute("msgError", "loginAndRegister.reset.msgErrorInvalid");
			break;
		case VALID:
			userService.updatePasswordByToken(resetPasswordDto);
	    	redirectAttributes.addFlashAttribute("msgSuccess", "loginAndRegister.reset.msgSuccess");
			break;	
		default:
			throw new WorkstocksBusinessException("Unable to valid token");
		}
	    
	    return "redirect:"+Routes.LOGIN;
	    
	}

}
