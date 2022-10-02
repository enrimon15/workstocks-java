package it.workstocks.rest;

import static it.workstocks.utils.AuthUtility.IS_APPLICANT;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.workstocks.dto.utility.CheckResultDto;
import it.workstocks.exception.WorkstocksBusinessException;

@RequestMapping(path = "v1/applicants/{applicantId}/job-alerts")
public interface ApplicantJobAlertV1 {
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "JOB-ALERTS")
	@Operation(summary = "Add user job-alert for a company")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or company not found", content = @Content),
		@ApiResponse(responseCode = "409", description = "Job-alert is already added"),
		@ApiResponse(responseCode = "403", description = "Access denied to add job-alert"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "/{companyId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addJobAlert(@PathVariable("applicantId") Long applicantId,
			@PathVariable("companyId") Long companyId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "JOB-ALERTS")
	@Operation(summary = "Remove user job-alert for a company")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant, company or job-alert not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to delete job-alert"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@DeleteMapping("/{companyId}")
	ResponseEntity<Void> deleteJobAlert(@PathVariable("applicantId") Long applicantId,
			@PathVariable("companyId") Long companyId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "JOB-ALERTS")
	@Operation(summary = "Check if user has subscribed a job-alert for a company")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or company not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to check job-alert"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CheckResultDto> checkJobAlert(@PathVariable("applicantId") Long applicantId,
			@PathVariable("companyId") Long companyId) throws WorkstocksBusinessException;

}
