package it.workstocks.presentation.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.presentation.Routes;
import it.workstocks.presentation.Templates;
import it.workstocks.service.ApplicationService;
import it.workstocks.service.JobOfferService;
import it.workstocks.utils.AuthUtility;

@Controller
@RequestMapping(path = Routes.ROOT_HANDLE_OFFERS)
public class CompanyProfileOffers {

	@Autowired
	private JobOfferService jobOfferService;
	
	@Autowired
	private ApplicationService applicationService;

	@GetMapping
	public String handleOffers(@RequestParam(defaultValue = "1") Integer page, Model model) throws WorkstocksBusinessException {
		model.addAttribute("jobs",
				jobOfferService.findByCompanyId(AuthUtility.getCurrentCompanyOwner().getCompany().getId(), page)
		);
		return Templates.COMPANY_OWNER_PROFILE.getTemplate();
	}

	@GetMapping(Routes.DELETE)
	public String deleteJobOffer(@RequestParam Long id, RedirectAttributes redirectAttributes)
			throws WorkstocksBusinessException {
		jobOfferService.deleteById(id);
		redirectAttributes.addFlashAttribute("msgSuccess", "mj.successDelete");
		return "redirect:"+Routes.ROOT_HANDLE_OFFERS;
	}

	@GetMapping(Routes.CANDIDATES)
	public String getJobOfferCandidates(@RequestParam(defaultValue = "1") Integer page, @RequestParam Long jobId, @RequestParam String jobName, Model model)
			throws WorkstocksBusinessException {
		model.addAttribute("jobName", jobName);
		model.addAttribute("jobId", jobId);
		model.addAttribute("candidates", applicationService.findCandidatesByJobId(jobId,page));
		return Templates.COMPANY_OWNER_PROFILE.getTemplate();
	}

}
