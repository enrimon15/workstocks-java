package it.workstocks.rest.impl;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.id.CompanyIdDto;
import it.workstocks.dto.utility.CheckResultDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.rest.ApplicantJobAlertV1;
import it.workstocks.service.ApplicantService;
import it.workstocks.service.CompanyService;
import it.workstocks.service.JobAlertService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;

@RestController
public class ApplicantJobAlertV1Impl implements ApplicantJobAlertV1 {
	
	@Autowired
	private ApplicantService applicantService;
	
	@Autowired
	private CompanyService companyService;

	@Autowired
	private WorkstocksProperties prop;

	@Autowired
	private JobAlertService jobAlertService;
	
	@Autowired
	private Translator translator; 

	private UriComponentsBuilder uriBuilder;
	
	@PostConstruct
	private void prepareBaseUri() {
		uriBuilder = UriComponentsBuilder.newInstance().path(prop.getSite().getUrl() + "/v1/applicants/{applicantId}/job-alerts");
	}
	
	private void checkForJobAlert(Long applicantId, Long companyId) throws WorkstocksBusinessException {
		applicantService.checkApplicantExistenceById(applicantId);
		companyService.checkCompanyExistence(companyId);

		if (!AuthUtility.compareCurrentUser(applicantId)) {
			throw new AccessDeniedException(
					String.format("User not authorized to access job alert for Applicant with id %d", applicantId));
		}
	}

	@Override
	public ResponseEntity<Void> addJobAlert(Long applicantId, CompanyIdDto companyIdDto, Errors errors)
			throws WorkstocksBusinessException {

		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"company id"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}

		checkForJobAlert(applicantId, companyIdDto.getCompanyId());

		return ResponseEntity.created(uriBuilder.cloneBuilder().path("/{companyId}")
				.buildAndExpand(applicantId,
						jobAlertService.addOrRemoveJobAlert(companyIdDto.getCompanyId(), applicantId, true))
				.toUri()).build();
	}

	@Override
	public ResponseEntity<Void> deleteJobAlert(Long applicantId, Long companyId) throws WorkstocksBusinessException {
		checkForJobAlert(applicantId, companyId);
		jobAlertService.addOrRemoveJobAlert(companyId, applicantId, false);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<CheckResultDto> checkJobAlert(Long applicantId, Long companyId) throws WorkstocksBusinessException {
		checkForJobAlert(applicantId, companyId);
		return ResponseEntity.ok(new CheckResultDto(jobAlertService.isJobAlertApplicaredByApplicant(companyId, applicantId)));
	}

}
