package it.workstocks.presentation.applicant;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.workstocks.dto.user.applicant.SimpleApplicantDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.presentation.Routes;
import it.workstocks.presentation.Templates;
import it.workstocks.service.ApplicantService;
import it.workstocks.service.ApplicationService;
import it.workstocks.service.JobOfferService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.FileUtils;

@Controller
@RequestMapping(path = Routes.ROOT_APPLICANT)
public class ApplicantProfile {
	
	@Autowired
	private ApplicantService applicantService;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private JobOfferService jobOfferService;

	@GetMapping(Routes.PROFILE)
	public String getUserProfile(Model model) throws WorkstocksBusinessException {	
		SimpleApplicantDto applicantDto = applicantService.findApplicantById(AuthUtility.getCurrentApplicant().getId());
		model.addAttribute("user", applicantDto);
		return Templates.APPLICANT_PROFILE.getTemplate();
	}
	
	@PostMapping(Routes.PROFILE)
	public String updateProfile(@Valid @ModelAttribute("user") SimpleApplicantDto applicantDto, Errors errors, RedirectAttributes redirectAttributes, HttpServletRequest req) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			return Templates.APPLICANT_PROFILE.getTemplate();
		}
		
		if (!AuthUtility.compareCurrentUser(applicantDto.getId())) {
			throw new AccessDeniedException("user Unauthorized!");
		}
		
		try {
			byte[] avatar = FileUtils.getBinaryData(req.getPart("avatarPhoto"));
			if (avatar != null) applicantDto.setAvatar(avatar);
		} catch (IOException | ServletException e) {
			throw new WorkstocksBusinessException("Failed to retrieve applicant avatar image from front-end");
		}
		
		applicantService.updateApplicantProfile(applicantDto);
		redirectAttributes.addFlashAttribute("successMsg", "user.profile.successMsg");
		return "redirect:"+Routes.ROOT_APPLICANT+Routes.PROFILE;
	}
	
	@GetMapping(Routes.FAVORITES)
	public String getFavorite(@RequestParam(defaultValue = "1") Integer page, Model model) throws WorkstocksBusinessException {
		model.addAttribute("favourites", jobOfferService.findFavoritesByApplicantId(AuthUtility.getCurrentApplicant().getId(), page));
		return Templates.APPLICANT_PROFILE.getTemplate();
	}
	
	@GetMapping(Routes.FAVORITES_DELETE)
	public String deleteFavorite(@RequestParam("jobId") Long jobId, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		jobOfferService.addOrRemoveApplicantFavorite(true, AuthUtility.getCurrentApplicant().getId(), jobId);
		redirectAttributes.addFlashAttribute("msgSuccess", "fv.successDelete");
		return "redirect:"+Routes.ROOT_APPLICANT+Routes.FAVORITES;
	}
	
	@GetMapping(Routes.APPLIED_JOBS)
	public String getApplications(@RequestParam(defaultValue = "1") Integer pageNumber, Model model) throws WorkstocksBusinessException {
		model.addAttribute("applications", applicationService.findApplicationsByApplicantId(AuthUtility.getCurrentApplicant().getId(), pageNumber));
		return Templates.APPLICANT_PROFILE.getTemplate();
	}
	
	@GetMapping(Routes.APPLIED_JOBS_DELETE)
	public String deleteApplications(@RequestParam("jobId") Long jobId, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		applicationService.removeApplicantApplicationByJob(jobId, AuthUtility.getCurrentApplicant().getId());	
		redirectAttributes.addFlashAttribute("msgSuccess", "aj.successDelete");
		return "redirect:"+Routes.ROOT_APPLICANT+Routes.APPLIED_JOBS;
	}
	


}
