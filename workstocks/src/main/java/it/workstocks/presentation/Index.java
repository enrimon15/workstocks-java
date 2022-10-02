package it.workstocks.presentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.service.ApplicantService;
import it.workstocks.service.ApplicationService;
import it.workstocks.service.CompanyService;
import it.workstocks.service.JobOfferService;
import it.workstocks.service.NewsService;

@Controller
public class Index {

	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private JobOfferService jobOfferService;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private ApplicantService applicantService;

	@Autowired
	private NewsService newsService;
	
	@GetMapping
	public String index(Model model) throws WorkstocksBusinessException {
		model.addAttribute("companyCount", companyService.countAllCompany());
		model.addAttribute("jobCount", jobOfferService.countAllJobOffers());
		model.addAttribute("applicationCount", applicationService.countAllApplication());
		model.addAttribute("applicantCount", applicantService.countAllApplicant());
		model.addAttribute("jobs", applicationService.retrieveMostPopularJobOffers());
		model.addAttribute("mostRankedCompanies", companyService.findMostRatedCompanies());
		model.addAttribute("news", newsService.findLatests(3));

		return Templates.INDEX.getTemplate();
	}
}
