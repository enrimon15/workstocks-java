package it.workstocks.presentation.applicant;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.workstocks.dto.review.ReviewDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.presentation.Routes;
import it.workstocks.service.ReviewService;

@RequestMapping(path = Routes.ROOT_REVIEW)
@Controller
public class ApplicantReview {
	
	@Autowired
	ReviewService reviewService;
	
	@PostMapping
	public String upsertReview(@Valid @ModelAttribute("review") ReviewDto reviewDto, Errors errors, @RequestHeader String referer, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			redirectAttributes.addFlashAttribute("msgError", "cp.reviewError");
			return "redirect:" + referer;
		}
		
		reviewService.addOrUpdateReview(reviewDto);
		redirectAttributes.addFlashAttribute("msgSuccess", "cp.reviewSuccess");
		return "redirect:" + referer;
	}

}
