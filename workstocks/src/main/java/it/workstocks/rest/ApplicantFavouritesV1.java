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

@RequestMapping(path =  "v1/applicants/{applicantId}/favourites")
public interface ApplicantFavouritesV1 {
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "FAVOURITES")
	@Operation(summary = "Get paginated user favourites job offer")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameter", content = @Content),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to get favourites", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PaginatedDtoResponse<SimpleJobOfferDto>> getApplicantFavouritesJobOffers(@PathVariable("applicantId") Long applicantId, Integer pgae, Integer limit)
			throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "FAVOURITES")
	@Operation(summary = "Add favourite job offer for a user")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Operation success", content = @Content),
		@ApiResponse(responseCode = "404", description = "Applicant or job offer not found", content = @Content),
		@ApiResponse(responseCode = "409", description = "Favourite is already added", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to add favourite", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addApplicantFavouriteJobOffer(@PathVariable("applicantId") Long applicantId,
			@Valid @RequestBody JobOfferIdDto jobOfferIdDto, Errors errors) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "FAVOURITES")
	@Operation(summary = "Remove favourite job offer for a user")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success", content = @Content),
		@ApiResponse(responseCode = "404", description = "Applicant, job offer or favourite not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to remove favourite", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@DeleteMapping("{jobOfferId}")
	ResponseEntity<Void> deleteApplicantFavouriteJobOffer(@PathVariable("applicantId") Long applicantId,
			@PathVariable("jobOfferId") Long jobOfferId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "FAVOURITES")
	@Operation(summary = "Check if exists favourite job offer for a user")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or job offer not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to check favourite", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@GetMapping(path = "{jobOfferId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CheckResultDto> checkApplicantFavouriteJobOffer(@PathVariable("applicantId") Long applicantId,
			@PathVariable("jobOfferId") Long jobOfferId) throws WorkstocksBusinessException;

}