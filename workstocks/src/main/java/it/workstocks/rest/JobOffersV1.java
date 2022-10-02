package it.workstocks.rest;

import static it.workstocks.utils.AuthUtility.PERMIT_ALL;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.utility.CountResultDto;
import it.workstocks.exception.WorkstocksBusinessException;

@RequestMapping(path = "v1/job-offers")
public interface JobOffersV1 {
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "JOB-OFFER")
	@Operation(summary = "Get list of filtered and paginated job offers (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameter"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "search", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PaginatedDtoResponse<SimpleJobOfferDto>> searchJobOffers(@RequestParam(required = false) String description,@RequestParam(required = false) String address, @RequestParam(required = false) String company,
			@RequestParam(required = false) String skill,@RequestParam(required = false) Integer salary,@RequestParam(required = false) Integer exprerience,@RequestParam(value = "contract-type", required = false) String contractType,
			@RequestParam Integer page,@RequestParam Integer limit) throws WorkstocksBusinessException;
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "JOB-OFFER")
	@Operation(summary = "Get job offers by id (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Job offer not found"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{jobOfferId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<JobOfferDto> getJobOfferById(@PathVariable("jobOfferId") Long jobOfferId) throws WorkstocksBusinessException;
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "JOB-OFFER")
	@Operation(summary = "Count total number of job offers (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "count", produces = MediaType.APPLICATION_JSON_VALUE) 
	ResponseEntity<CountResultDto> countJobOffers();
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "JOB-OFFER")
	@Operation(summary = "Get most popular job offers based on applications (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "popular", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Set<SimpleJobOfferDto>> getPopularJobOffers(@RequestParam Integer limit) throws WorkstocksBusinessException;

}
