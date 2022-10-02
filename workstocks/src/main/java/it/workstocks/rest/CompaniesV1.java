package it.workstocks.rest;

import static it.workstocks.utils.AuthUtility.IS_APPLICANT;
import static it.workstocks.utils.AuthUtility.PERMIT_ALL;

import java.util.Set;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.review.ReviewDto;
import it.workstocks.dto.user.company.CompanyDto;
import it.workstocks.dto.user.company.SimpleCompanyDto;
import it.workstocks.dto.user.company.WorkingPlaceDto;
import it.workstocks.dto.utility.CountResultDto;
import it.workstocks.exception.WorkstocksBusinessException;

@RequestMapping(path = "v1/companies")
public interface CompaniesV1 {
		
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "COMPANY")
	@Operation(summary = "Get list of filtered and paginated companies (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameter"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "search", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PaginatedDtoResponse<SimpleCompanyDto>> searchCompanies(@RequestParam(required = false) String name,@RequestParam(required = false) String address, @RequestParam(value = "foundation-year", required = false) Integer foundationYear,
			@RequestParam(value = "employees-number", required = false) Integer employeesNumber, @RequestParam Integer page, @RequestParam Integer limit) throws WorkstocksBusinessException;

	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "COMPANY")
	@Operation(summary = "Get company by id (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Company not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path= "{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CompanyDto> getCompanyById(@PathVariable("companyId") Long companyId) throws WorkstocksBusinessException;
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "COMPANY")
	@Operation(summary = "Get company photo by id (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Company or photo not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path= "{companyId}/photo", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
	ResponseEntity<byte[]> getCompanyPhotoById(@PathVariable("companyId") Long companyId) throws WorkstocksBusinessException;
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "COMPANY")
	@Operation(summary = "Get working places by company id (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Company not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{companyId}/working-places", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Set<WorkingPlaceDto>> getWorkingPlacesByCompanyId(@PathVariable("companyId") Long companyId) throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "COMPANY")
	@Operation(summary = "Get company review by applicant")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Company not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{companyId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ReviewDto> getCompanyReviewByLoggedApplicant(@PathVariable("companyId") Long companyId) throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "COMPANY")
	@Operation(summary = "Add company review")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Review successfully added"),
		@ApiResponse(responseCode = "400", description = "Wrong review payload"),
		@ApiResponse(responseCode = "404", description = "Company or review not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "{companyId}/reviews", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addCompanyReview(@PathVariable("companyId") Long companyId, @Valid @RequestBody ReviewDto review, Errors errors) throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "COMPANY")
	@Operation(summary = "Update company review")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Review successfully updated"),
		@ApiResponse(responseCode = "400", description = "Wrong review payload"),
		@ApiResponse(responseCode = "404", description = "Company or review not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PutMapping(path = "{companyId}/reviews", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> updateCompanyReview(@PathVariable("companyId") Long companyId, @Valid @RequestBody ReviewDto review, Errors errors) throws WorkstocksBusinessException;
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "COMPANY")
	@Operation(summary = "Count total number of companies (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "count", produces = MediaType.APPLICATION_JSON_VALUE) 
	ResponseEntity<CountResultDto> countCompanies();
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "COMPANY")
	@Operation(summary = "Get five most popular companies based on reviews (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameter"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "popular", produces = MediaType.APPLICATION_JSON_VALUE) 
	ResponseEntity<Set<SimpleCompanyDto>> getPopularCompanies(@RequestParam Integer limit) throws WorkstocksBusinessException;
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "COMPANY")
	@Operation(summary = "Get company job offers (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Company not found",content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{companyId}/job-offers", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Set<SimpleJobOfferDto>> getCompanyJobOffers(@PathVariable("companyId") Long companyId, @RequestParam Integer limit) throws WorkstocksBusinessException;
	
}
