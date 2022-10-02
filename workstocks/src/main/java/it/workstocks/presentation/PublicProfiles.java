package it.workstocks.presentation;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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

import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.dto.review.ReviewDto;
import it.workstocks.dto.review.ReviewKeyDto;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.user.applicant.ApplicantDto;
import it.workstocks.dto.user.applicant.SimpleApplicantDto;
import it.workstocks.dto.user.company.CompanyDto;
import it.workstocks.dto.user.company.CompanyOwnerDto;
import it.workstocks.entity.pojo.ReviewAverageAndCountPojo;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.security.Roles;
import it.workstocks.service.ApplicantService;
import it.workstocks.service.CompanyService;
import it.workstocks.service.JobAlertService;
import it.workstocks.service.JobOfferService;
import it.workstocks.service.ProfileService;
import it.workstocks.service.ReviewService;
import it.workstocks.utils.AuthUtility;

@Controller
@RequestMapping(path = Routes.ROOT_PUBLIC)
public class PublicProfiles {
	@Autowired
	private ProfileService profileService;

	@Autowired
	private JobOfferService jobOfferService;

	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private ApplicantService applicantService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private JobAlertService jobAlertService;
	
	@Autowired
	private TemplateEngine templateEngine;

	@GetMapping(Routes.APPLICANT_PUBLIC_PAGE)
	public String getUserProfile(@RequestParam("id") Long id, Model model) throws WorkstocksBusinessException {
		ApplicantDto dto = (ApplicantDto) profileService.loadFullUserById(id, ApplicantDto.class);
		model.addAttribute("user", dto);
		return Templates.APPLICANT_PUBLIC_PROFILE.getTemplate();
	}

	@GetMapping(Routes.COMPANY_PUBLIC_PAGE)
	public String getCompanyProfile(@RequestParam("id") Long id, Model model) throws WorkstocksBusinessException {
		CompanyOwnerDto companyOwnerDto = (CompanyOwnerDto) profileService.loadFullUserById(id, CompanyOwnerDto.class);
		
		ReviewDto review = null;
		if (AuthUtility.hasRole(Roles.APPLICANT)) {
			ReviewKeyDto key = new ReviewKeyDto(AuthUtility.getCurrentApplicant().getId(), companyOwnerDto.getCompany().getId());
			ReviewDto rev = reviewService.findById(key);
			review = rev != null ? rev : new ReviewDto(key);
		} else {
			review = new ReviewDto();
		}
		
		ReviewAverageAndCountPojo ratingStats = reviewService.findAverageRatingByCompanyId(companyOwnerDto.getCompany().getId());
		companyOwnerDto.getCompany().setRatingStats(ratingStats);
		
		model.addAttribute("user", companyOwnerDto);
		model.addAttribute("isJobAlert", AuthUtility.hasRole(Roles.APPLICANT) ? jobAlertService.isJobAlertApplicaredByApplicant(companyOwnerDto.getCompany().getId(), AuthUtility.getCurrentApplicant().getId()) : false);
		model.addAttribute("review", review);
		model.addAttribute("latestJobs",
				jobOfferService.findLatestsByCompanyId(companyOwnerDto.getCompany().getId(), 4));

		return Templates.COMPANY_PUBLIC_PROFILE.getTemplate();
	}
	
	@GetMapping(Routes.DOWNLOAD_CV)
	public void downloadCv(@RequestParam(required = false) Long userId, HttpServletResponse response) throws WorkstocksBusinessException {
		userId = userId != null ? userId : AuthUtility.getCurrentApplicant().getId();
		SimpleApplicantDto applicant = applicantService.findApplicantById(userId);
		
		byte[] rawFile = applicant.getCurricula();	
		if (rawFile == null || rawFile.length <= 0) return;

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment;filename=" + "CV-" + applicant.getName() + "-" + applicant.getSurname() + ".pdf");
		OutputStream out;
		try {
			out = response.getOutputStream();
			out.write(rawFile);
		} catch (IOException e) {
			throw new WorkstocksBusinessException("failed to download cv file", e);
		}
		
	}

	/**
	 * Page for companies search
	 * 
	 * @return
	 */
	@GetMapping(Routes.COMPANIES)
	public String getCompanySearchPage() {
		return Templates.COMPANIES.getTemplate();
	}

	/**
	 * Search data for companies with ajax
	 * 
	 * @return
	 */
	@PostMapping(Routes.COMPANIES)
	public ResponseEntity<PaginatedResponse> getAjaxResultsForCompanySearch(@RequestBody PaginatedRequest request) throws WorkstocksBusinessException {
		PaginatedDtoResponse<CompanyDto> resp = companyService.searchCompanies(request);
		
		Context context = new Context();
		context.setLocale(LocaleContextHolder.getLocale());

		Map<String, Object> variables = new HashMap<>();
		variables.put("companies", resp.getElements());
		variables.put("totalResults", resp.getResponse().getTotalElements());
		context.setVariables(variables);

		PaginatedResponse result = resp.getResponse();
		result.setData(templateEngine.process(Templates.COMPANIES_DATA.getTemplate(), context));
		

		return ResponseEntity.ok(result);
	}

	/**
	 * Page for applicants search
	 * 
	 * @return
	 */
	@GetMapping(Routes.APPLICANTS)
	public String getApplicantSearchPage() {
		return Templates.APPLICANTS.getTemplate();
	}

	/**
	 * Search data for applicants with ajax
	 * 
	 * @return
	 */
	@PostMapping(Routes.APPLICANTS)
	public ResponseEntity<PaginatedResponse> getAjaxResultsForApplicantSearch(@RequestBody PaginatedRequest request) throws WorkstocksBusinessException {
		PaginatedDtoResponse<SimpleApplicantDto> resp = applicantService.searchApplicants(request);
		
		Context context = new Context();
		context.setLocale(LocaleContextHolder.getLocale());

		Map<String, Object> variables = new HashMap<>();
		variables.put("subscribers", resp.getElements());
		variables.put("totalResults", resp.getResponse().getTotalElements());
		variables.put("routeToApplicantDetail", Routes.APPLICANT_PUBLIC_PAGE);
		context.setVariables(variables);

		PaginatedResponse result = resp.getResponse();
		result.setData(templateEngine.process(Templates.APPLICANTS_DATA.getTemplate(), context));
		
		return ResponseEntity.ok(result);
	}
}
