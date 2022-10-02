package it.workstocks.presentation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.entity.enums.SkillType;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.security.Roles;
import it.workstocks.service.ApplicationService;
import it.workstocks.service.JobOfferService;
import it.workstocks.service.SkillService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.StringUtils;

@Controller
@RequestMapping(path = Routes.ROOT_PUBLIC)
public class JobOffers {

	@Autowired
	private JobOfferService jobOfferService;
	
	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private SkillService skillService;
	
	@Autowired
	private TemplateEngine templateEngine;
	

	@GetMapping(Routes.JOB_OFFER)
	public String getJobDetails(@RequestParam("id") Long id, Model model) throws WorkstocksBusinessException {
		JobOfferDto dto = jobOfferService.findById(id);
		
		model.addAttribute("applicationsSize", applicationService.countApplicationsByJobId(id));

		model.addAttribute("isApplicated",
				AuthUtility.hasRole(Roles.APPLICANT)
						? applicationService.isJobOfferAppliedForApplicant(dto.getId(),
								AuthUtility.getCurrentApplicant().getId())
						: false);

		model.addAttribute("isFavorite",
				AuthUtility.hasRole(Roles.APPLICANT)
						? jobOfferService.isFavoriteForApplicant(dto.getId(), AuthUtility.getCurrentApplicant().getId())
						: false);

		model.addAttribute("jobOffer", dto);
		return Templates.JOB_OFFER_DETAILS.getTemplate();
	}

	/**
	 * Page for job search
	 * 
	 * @return
	 */
	@GetMapping(Routes.JOB_OFFERS)
	public String getAllJobs(@RequestParam(name= "company", required = false) String company, @RequestParam(name= "title", required = false) String title,@RequestParam( name = "address", required = false) String address, Model model) throws WorkstocksBusinessException {
		if(StringUtils.isNotBlank(title)) {
			model.addAttribute("title", title);
		}
		
		if(StringUtils.isNotBlank(address)) {
			model.addAttribute("address", address);
		}
		
		if(StringUtils.isNotBlank(company)) {
			model.addAttribute("company", company);
		}
		model.addAttribute("skills", skillService.getPopularSkills(SkillType.JOB_OFFER));
		return Templates.JOB_OFFERS.getTemplate();
	}

	/**
	 * Search data for jobs with ajax
	 * 
	 * @return
	 */
	@PostMapping(Routes.JOB_OFFERS)
	public ResponseEntity<PaginatedResponse> getAjaxResults(@RequestBody PaginatedRequest request) throws WorkstocksBusinessException {
		PaginatedDtoResponse<JobOfferDto> resp = jobOfferService.searchJobOffers(request);
		
		Context context = new Context();
		context.setLocale(LocaleContextHolder.getLocale());

		Map<String, Object> variables = new HashMap<>();
		variables.put("jobs", resp.getElements());
		variables.put("totalResults", resp.getResponse().getTotalElements());
		variables.put("routeToJobDetail", Routes.JOB_OFFER_DETAIL_PAGE);
		context.setVariables(variables);

		PaginatedResponse result = resp.getResponse();
		result.setData(templateEngine.process(Templates.JOB_OFFERS_DATA.getTemplate(), context));

		return ResponseEntity.ok(result);
	}

}
