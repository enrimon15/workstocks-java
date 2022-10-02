package it.workstocks.rest.impl;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.FiltersDto;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.utility.CountResultDto;
import it.workstocks.entity.enums.ContractType;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.rest.JobOffersV1;
import it.workstocks.service.ApplicationService;
import it.workstocks.service.JobOfferService;
import it.workstocks.validator.QueryParamValidator;

@RestController()
public class JobOffersV1Impl implements JobOffersV1 {
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private JobOfferService jobOfferService;
	
	@Autowired
	private WorkstocksProperties prop;
	
	private UriComponentsBuilder uriBuilder;
	
	@Autowired
	private QueryParamValidator queryParamValidator;
	
	@PostConstruct
	private void prepareBaseUri() {
		uriBuilder = UriComponentsBuilder.newInstance().path(prop.getSite().getUrl() + "/v1/job-offers");
	}

	@SuppressWarnings("static-access")
	@Override
	public ResponseEntity<PaginatedDtoResponse<SimpleJobOfferDto>> searchJobOffers(String description, String address, String company, String skill,
			Integer salary, Integer exprerience, String contractType, Integer page, Integer limit) throws WorkstocksBusinessException {
		queryParamValidator.validateInteger("limit", limit, 1, 10);
		PaginatedRequest request = buildFilterRequest(description, address, company, skill, salary, exprerience, contractType, page, limit);		
		PaginatedDtoResponse<SimpleJobOfferDto> jobOffers = jobOfferService.searchJobOffers(request);
		for (SimpleJobOfferDto job : jobOffers.getElements()) {
			job.setDetailsURL(uriBuilder.cloneBuilder().path("/{jobOfferId}").buildAndExpand(job.getId()).toString());
			job.getCompany().setDetailsURL(
					uriBuilder.fromPath(prop.getSite().getUrl() + "/v1/companies/{companyId}").buildAndExpand(job.getCompany().getId()).toString()
			);
		}
			
		return ResponseEntity.ok(jobOffers);
	}
	
	private PaginatedRequest buildFilterRequest(String description, String address, String company,
			String skill, Integer salary, Integer experience, String contractType, Integer page, Integer limit) {
		PaginatedRequest request = new PaginatedRequest();
		request.setPageNumber(page);
		request.setPageSize(limit);
		FiltersDto filters = new FiltersDto();
		filters.setJobTitle(description);
		filters.setAddress(address);
		filters.setCompanyName(company);
		filters.setSkill(skill);
		filters.setSalary(salary);
		filters.setExperience(experience);
		filters.setOfferType(contractType != null ? ContractType.valueOf(contractType) : null);
		request.setFilters(filters);
		return request;
	}

	@SuppressWarnings("static-access")
	@Override
	public ResponseEntity<JobOfferDto> getJobOfferById(Long jobOfferId) throws WorkstocksBusinessException {
		JobOfferDto job = jobOfferService.findById(jobOfferId);
		job.getCompany().setDetailsURL(
				uriBuilder.fromPath(prop.getSite().getUrl() + "/v1/companies/{companyId}").buildAndExpand(job.getCompany().getId()).toString()
		);
		job.setApplicationsSize(applicationService.countApplicationsByJobId(jobOfferId));
		return ResponseEntity.ok(job);
	}

	@Override
	public ResponseEntity<CountResultDto> countJobOffers() {
		return ResponseEntity.ok(new CountResultDto(jobOfferService.countAllJobOffers()));
	}

	@SuppressWarnings("static-access")
	@Override
	public ResponseEntity<Set<SimpleJobOfferDto>> getPopularJobOffers(Integer limit) throws WorkstocksBusinessException {
		queryParamValidator.validateInteger("limit", limit, 1, 10);
		Set<SimpleJobOfferDto> jobs = applicationService.retrieveMostPopularJobOffers(limit);
		for (SimpleJobOfferDto job : jobs) {
			job.setDetailsURL(
					uriBuilder.fromPath(prop.getSite().getUrl() + "/v1/job-offer/{jobOfferId}").buildAndExpand(job.getId()).toString());
			job.getCompany().setDetailsURL(
					uriBuilder.fromPath(prop.getSite().getUrl() + "/v1/companies/{companyId}").buildAndExpand(job.getCompany().getId()).toString());
		}
		return ResponseEntity.ok(jobs);
	}
	
}
