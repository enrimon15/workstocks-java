package it.workstocks.rest;

import static it.workstocks.utils.AuthUtility.IS_APPLICANT;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.workstocks.dto.id.JobOfferIdDto;
import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.utility.CheckResultDto;
import it.workstocks.exception.WorkstocksBusinessException;

@RequestMapping("v1/applicants/{applicantId}/applications")
public interface ApplicantApplicationsV1 {
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICATIONS")
	@Operation(summary = "Add user application for a job offer")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Operation success", content = @Content),
		@ApiResponse(responseCode = "404", description = "Applicant or job offer not found", content = @Content),
		@ApiResponse(responseCode = "409", description = "Application is already added", content = @Content),
		@ApiResponse(responseCode = "422", description = "Application is no longer active (expired)", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to add application", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addApplicantApplication(@PathVariable("applicantId") Long applicantId,
			@Valid @RequestBody JobOfferIdDto jobOfferIdDto, Errors errors) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICATIONS")
	@Operation(summary = "Remove user application for a job offer")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success", content = @Content),
		@ApiResponse(responseCode = "404", description = "Applicant, job offer or application not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to add application", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@DeleteMapping("/{jobOfferId}")
	ResponseEntity<Void> deleteApplicantApplication(@PathVariable("applicantId") Long applicantId,
			@PathVariable("jobOfferId") Long jobOfferId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICATIONS")
	@Operation(summary = "Get paginated user applications")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameter", content = @Content),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to get application", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PaginatedDtoResponse<SimpleJobOfferDto>> getApplicantApplications(@PathVariable("applicantId") Long applicantId, @RequestParam Integer page, @RequestParam Integer limit)
			throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICATIONS")
	@Operation(summary = "Check if exists user applications")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or job offer not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to check application", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@GetMapping(path = "/{jobOfferId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CheckResultDto> checkApplicantApplication(@PathVariable("applicantId") Long applicantId,
			@PathVariable("jobOfferId") Long jobOfferId) throws WorkstocksBusinessException;

}
