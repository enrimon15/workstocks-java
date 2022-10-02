package it.workstocks.rest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import it.workstocks.dto.utility.CountResultDto;
import it.workstocks.rest.JobApplicationsV1;
import it.workstocks.service.ApplicationService;

@RestController()
public class JobApplicationsV1Impl implements JobApplicationsV1 {
	
	@Autowired
	private ApplicationService applicationService;

	@Override
	public ResponseEntity<CountResultDto> countApplications() {
		return ResponseEntity.ok(new CountResultDto(applicationService.countAllApplication()));
	}

}
