package it.workstocks.rest.impl;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.review.ReviewDto;
import it.workstocks.dto.search.FiltersDto;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.user.company.CompanyDto;
import it.workstocks.dto.user.company.SimpleCompanyDto;
import it.workstocks.dto.user.company.WorkingPlaceDto;
import it.workstocks.dto.utility.CountResultDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.rest.CompaniesV1;
import it.workstocks.service.CompanyService;
import it.workstocks.service.JobOfferService;
import it.workstocks.service.ReviewService;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;
import it.workstocks.validator.QueryParamValidator;

@RestController
public class CompaniesRestServicesV1Impl implements CompaniesV1 {
	
	@Autowired
	private CompanyService companyService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private JobOfferService jobService;
	
	@Autowired
	private WorkstocksProperties prop;
	
	@Autowired
	private QueryParamValidator queryParamValidator;
	
	@Autowired
	private Translator translator;
	
	private UriComponentsBuilder uriBuilder;
	
	@PostConstruct
	private void prepareBaseUri() {
		uriBuilder = UriComponentsBuilder.newInstance().path(prop.getSite().getUrl() + "/v1/companies");
	}

	@Override
	public ResponseEntity<PaginatedDtoResponse<SimpleCompanyDto>> searchCompanies(String name, String address, Integer foundationYear,
			Integer employeesNumber, Integer page, Integer limit) throws WorkstocksBusinessException {
		queryParamValidator.validateInteger("limit", limit, 1, 10); 
		PaginatedRequest request = buildFilterRequest(name, address, foundationYear, employeesNumber, page, limit);		
		PaginatedDtoResponse<SimpleCompanyDto> companies = companyService.searchCompanies(request);
		for (SimpleCompanyDto company : companies.getElements()) {
			company.setDetailsURL(uriBuilder.cloneBuilder().path("/{companyId}").buildAndExpand(company.getId()).toString());
		}
			
		return ResponseEntity.ok(companies);
	}

	private PaginatedRequest buildFilterRequest(String name, String address, Integer foundationYear,
			Integer employeesNumber, Integer page, Integer limit) {
		PaginatedRequest request = new PaginatedRequest();
		request.setPageNumber(page);
		request.setPageSize(limit);
		FiltersDto filters = new FiltersDto();
		filters.setName(name);
		filters.setAddress(address);
		filters.setFoundationYear(foundationYear);
		filters.setEmployeesNumber(employeesNumber);
		request.setFilters(filters);
		return request;
	}

	@Override
	public ResponseEntity<CompanyDto> getCompanyById(Long companyId) throws WorkstocksBusinessException {
		CompanyDto company = companyService.findById(companyId);
		company.setRatingStats(reviewService.findAverageRatingByCompanyId(companyId));
		company.setJobOffers(jobService.findByCompanyId(companyId, 4));
		return ResponseEntity.ok(company);
	}
	
	@Override
	public ResponseEntity<byte[]> getCompanyPhotoById(Long companyId) throws WorkstocksBusinessException {
		byte[] photo = companyService.findPhotoById(companyId);
		if (photo == null || photo.length <= 0) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.PHOTO_NOT_FOUND, new Object[] {"company",companyId}), HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(photo);
	}
	
	@Override
	public ResponseEntity<Set<WorkingPlaceDto>> getWorkingPlacesByCompanyId(Long companyId) throws WorkstocksBusinessException {
		companyService.checkCompanyExistence(companyId);
		return ResponseEntity.ok(companyService.findWorkingPlacesByCompanyId(companyId));
	}

	@Override
	public ResponseEntity<ReviewDto> getCompanyReviewByLoggedApplicant(Long companyId) throws WorkstocksBusinessException {
		companyService.checkCompanyExistence(companyId);
		return ResponseEntity.ok(reviewService.findByCompanyId(companyId));
	}

	@Override
	public ResponseEntity<Void> addCompanyReview(Long companyId, ReviewDto review, Errors errors) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"review"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}
		
		companyService.checkCompanyExistence(companyId);
		reviewService.addReview(review, companyId);
		return ResponseEntity.created(uriBuilder
				.cloneBuilder()
				.path("/{companyId}/reviews")
				.buildAndExpand(companyId)
				.toUri()).build();
	}
	
	@Override
	public ResponseEntity<Void> updateCompanyReview(Long companyId, ReviewDto review, Errors errors) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"review"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}
		
		companyService.checkCompanyExistence(companyId);
		reviewService.upsertReview(review, companyId);
		return ResponseEntity.noContent().build();
	}


	@Override
	public ResponseEntity<CountResultDto> countCompanies() {
		return ResponseEntity.ok(new CountResultDto(companyService.countAllCompany()));
	}

	@Override
	public ResponseEntity<Set<SimpleCompanyDto>> getPopularCompanies(Integer limit) throws WorkstocksBusinessException {
		queryParamValidator.validateInteger("limit", limit, 1, 10); 
		Set<SimpleCompanyDto> companies = companyService.findMostRatedCompanies(limit);
		for (SimpleCompanyDto company : companies) {
			company.setDetailsURL(uriBuilder.cloneBuilder().path("/{companyId}").buildAndExpand(company.getId()).toString());
		}
		
		return ResponseEntity.ok(companies);
	}

	@SuppressWarnings("static-access")
	@Override
	public ResponseEntity<Set<SimpleJobOfferDto>> getCompanyJobOffers(Long companyId, Integer limit) throws WorkstocksBusinessException {	
		queryParamValidator.validateInteger("limit", limit, 1, 10); 
		companyService.checkCompanyExistence(companyId);
		
		Set<SimpleJobOfferDto> jobs = jobService.findByCompanyId(companyId, limit);
		for (SimpleJobOfferDto job : jobs) {
			job.setDetailsURL(
					uriBuilder.fromPath(prop.getSite().getUrl() + "/v1/job-offer/{jobOfferId}").buildAndExpand(job.getId()).toString());
			job.getCompany().setDetailsURL(
					uriBuilder.fromPath(prop.getSite().getUrl() + "/v1/companies/{companyId}").buildAndExpand(job.getCompany().getId()).toString());
		}
		
		return ResponseEntity.ok(jobs);
	}
}
