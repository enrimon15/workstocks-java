package it.workstocks.presentation.company;

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

import it.workstocks.dto.user.company.WorkingPlaceDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.presentation.Routes;
import it.workstocks.presentation.Templates;
import it.workstocks.service.CompanyService;
import it.workstocks.service.JobOfferService;
import it.workstocks.utils.AuthUtility;

@Controller
@RequestMapping(path = Routes.ROOT_WORKING_PLACES)
public class CompanyProfileWorkingPlaces {

	@Autowired
	CompanyService companyService;
	
	@Autowired
	JobOfferService jobOfferService;

	@GetMapping
	public String getWorkingPlaces(Model model) throws WorkstocksBusinessException {	
		model.addAttribute("workingPlaces", companyService.findWorkingPlacesByCompanyId(AuthUtility.getCurrentCompanyOwner().getCompany().getId()));
		model.addAttribute("workingPlace", new WorkingPlaceDto());
		return Templates.COMPANY_OWNER_PROFILE.getTemplate();
	}
	
	@PostMapping
	public String addWorkingPlaces(@Valid @ModelAttribute("workingPlace") WorkingPlaceDto workingPlaceDto, Errors errors, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {	
		if (errors.hasErrors()) {
			redirectAttributes.addFlashAttribute("msgError", "wp.error");
			return "redirect:"+Routes.ROOT_WORKING_PLACES;
		}
		
		if (companyService.isModifyMainWorkingPlace(workingPlaceDto)) {
			redirectAttributes.addFlashAttribute("msgError", "wp.errorMain");
			return "redirect:"+Routes.ROOT_WORKING_PLACES;
		}
		
		companyService.insertOrUpdateWorkingPlace(workingPlaceDto);
		redirectAttributes.addFlashAttribute("msgSuccess", workingPlaceDto.getId() != null ? "wp.successUpdate" : "wp.successAdd");
		return "redirect:"+Routes.ROOT_WORKING_PLACES;
	}
	
	@GetMapping(Routes.EDIT)
	public String getEditWorkingPlaces(@RequestParam Long id, Model model) throws WorkstocksBusinessException {	
		model.addAttribute("workingPlace", companyService.findWorkingPlaceById(id));	
		return Templates.COMPANY_OWNER_PROFILE.getTemplate();
	}
	
	@GetMapping(Routes.DELETE)
	public String deleteWorkingPlaces(@RequestParam Long id, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {	
		if (companyService.findWorkingPlaceById(id).isMainWorkingPlace()) {
			redirectAttributes.addFlashAttribute("msgError", "wp.errorMain");
			return "redirect:"+Routes.ROOT_WORKING_PLACES;
		}
		companyService.deleteWorkingPlaceById(id);
		redirectAttributes.addFlashAttribute("msgSuccess", "wp.successDelete");
		return "redirect:"+Routes.ROOT_WORKING_PLACES;
	}

	
}
