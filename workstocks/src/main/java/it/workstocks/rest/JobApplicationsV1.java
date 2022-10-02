package it.workstocks.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.workstocks.dto.utility.CountResultDto;

@RequestMapping(path = "v1/applications")
public interface JobApplicationsV1 {
	
	@Tag(name = "APPLICATIONS")
	@Operation(summary = "Count total number of job applications (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@PostMapping(path = "count", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CountResultDto> countApplications();
}
