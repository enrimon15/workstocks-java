package it.workstocks.presentation.company;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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

import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.user.company.SimpleCompanyOwnerDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.presentation.Routes;
import it.workstocks.presentation.Templates;
import it.workstocks.service.CompanyService;
import it.workstocks.service.JobOfferService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.FileUtils;

@Controller
@RequestMapping(path = Routes.ROOT_COMPANY)
public class CompanyProfile {

	@Autowired
	CompanyService companyService;
	
	@Autowired
	JobOfferService jobOfferService;
	
	@GetMapping(Routes.PROFILE)
	public String getCompanyOwnerProfile(Model model) throws WorkstocksBusinessException {
		SimpleCompanyOwnerDto companyOwner = companyService.findCompanyOwnerById(AuthUtility.getCurrentCompanyOwner().getId());
		model.addAttribute("user", companyOwner);
		return Templates.COMPANY_OWNER_PROFILE.getTemplate();
	}
	
	@PostMapping(Routes.PROFILE)
	public String updateCompanyOwnerProfile(@Valid @ModelAttribute("user") SimpleCompanyOwnerDto companyOwnerDto, Errors errors, RedirectAttributes redirectAttributes, HttpServletRequest req) throws WorkstocksBusinessException, IOException, ServletException {
		if (errors.hasErrors()) {
			return Templates.COMPANY_OWNER_PROFILE.getTemplate();
		}
		
		if (!AuthUtility.compareCurrentUser(companyOwnerDto.getId())) {
			throw new WorkstocksBusinessException("user Unauthorized!");
		}
		
		byte[] avatar = FileUtils.getBinaryData(req.getPart("avatarPhoto"));
		if (avatar != null) companyOwnerDto.setAvatar(avatar);
		
		companyService.updateCompanyOwnerProfile(companyOwnerDto);
		redirectAttributes.addFlashAttribute("successMsg", "profile.success");
		return "redirect:" + Routes.ROOT_COMPANY + Routes.PROFILE ;
	}

	
	@GetMapping(Routes.NEW_OFFER)
	public String newOffer(@RequestParam(name = "id", required = false) Long id, Model model) throws WorkstocksBusinessException {
		model.addAttribute("jobOffer", id == null ? new JobOfferDto() : jobOfferService.findById(id));
		model.addAttribute("workingPlaces", companyService.findWorkingPlacesByCompanyId(AuthUtility.getCurrentCompanyOwner().getCompany().getId()));
		return  Templates.COMPANY_OWNER_PROFILE.getTemplate();
	}
	
	@PostMapping(Routes.NEW_OFFER)
	public String addNewOffer(@Valid @ModelAttribute("jobOffer") JobOfferDto jobOfferDto, Errors errors, Model model, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			model.addAttribute("workingPlaces", companyService.findWorkingPlacesByCompanyId(AuthUtility.getCurrentCompanyOwner().getCompany().getId()));
			return Templates.COMPANY_OWNER_PROFILE.getTemplate();
		}
		
		jobOfferService.insertOrUpdateJobOffer(jobOfferDto);
		
		String msgSuccess = "nj.success";
		String redirectPage = "redirect:"+Routes.ROOT_COMPANY+Routes.NEW_OFFER;
		if (jobOfferDto.getId() != null) {
			msgSuccess = "nj.successUpdate";
			redirectPage = "redirect:"+Routes.ROOT_HANDLE_OFFERS;
		}
		redirectAttributes.addFlashAttribute("msgSuccess", msgSuccess);
		return redirectPage;
	}

	
}
