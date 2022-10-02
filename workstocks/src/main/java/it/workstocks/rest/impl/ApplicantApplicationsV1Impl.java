package it.workstocks.rest.impl;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.id.JobOfferIdDto;
import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.utility.CheckResultDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.rest.ApplicantApplicationsV1;
import it.workstocks.service.ApplicantService;
import it.workstocks.service.ApplicationService;
import it.workstocks.service.JobOfferService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;
import it.workstocks.validator.QueryParamValidator;

@RestController
public class ApplicantApplicationsV1Impl implements ApplicantApplicationsV1 {
	
	@Autowired
	private WorkstocksProperties prop;
	
	@Autowired
	private ApplicantService applicantService;
	
	@Autowired
	private JobOfferService jobOfferService;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private QueryParamValidator queryParamValidator;
	
	@Autowired
	private Translator translator;

	private UriComponentsBuilder uriBuilder;
	
	@PostConstruct
	private void prepareBaseUri() {
		uriBuilder = UriComponentsBuilder.newInstance().path(prop.getSite().getUrl() + "/v1/applicants/{applicantId}/applications");
	}
	
	private void checkForApplicant(Long applicantId) throws WorkstocksBusinessException {
		applicantService.checkApplicantExistenceById(applicantId);

		if (!AuthUtility.compareCurrentUser(applicantId)) {
			throw new AccessDeniedException(String
					.format("User not authorized to access application for Applicant with id %d", applicantId));
		}
	}
	
	private void checkForApplication(Long applicantId, Long jobOfferId) throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		jobOfferService.checkExistenceById(jobOfferId);
	}

	@Override
	public ResponseEntity<Void> addApplicantApplication(Long applicantId, JobOfferIdDto jobOfferIdDto, Errors errors)
			throws WorkstocksBusinessException {
		
		checkForApplicant(applicantId);

		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"job id"}), ErrorUtils.getErrorList(errors),HttpStatus.BAD_REQUEST);
		}

		JobOfferDto jobOffer = jobOfferService.findById(jobOfferIdDto.getJobOfferId());
		if (jobOffer.getDueDate().isBefore(LocalDate.now())) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.APPLICATION_EXPIRED, new Long[] {jobOfferIdDto.getJobOfferId()}), HttpStatus.UNPROCESSABLE_ENTITY);
		}

		applicationService.applyForApplicant(jobOfferIdDto.getJobOfferId(), applicantId);

		return ResponseEntity.created(uriBuilder.cloneBuilder().path("/{jobOfferId}")
				.buildAndExpand(applicantId, jobOfferIdDto.getJobOfferId()).toUri()).build();
	}

	@Override
	public ResponseEntity<Void> deleteApplicantApplication(Long applicantId, Long jobOfferId) throws WorkstocksBusinessException {
		checkForApplication(applicantId, jobOfferId);
		applicationService.removeApplicantApplicationByJob(jobOfferId, applicantId);
		return ResponseEntity.noContent().build();
	}

	@SuppressWarnings("static-access")
	@Override
	public ResponseEntity<PaginatedDtoResponse<SimpleJobOfferDto>> getApplicantApplications(Long applicantId, Integer page, Integer limit) throws WorkstocksBusinessException {
		queryParamValidator.validateInteger("limit", limit, 1, 10);
		checkForApplicant(applicantId);
		
		PaginatedDtoResponse<SimpleJobOfferDto> res = applicationService.findApplicationsByApplicantId(applicantId, page, limit);
		res.getElements().stream().forEach(job -> {
			job.setDetailsURL(uriBuilder.cloneBuilder().path("/{jobOfferId}").buildAndExpand(applicantId, job.getId()).toString());
			job.getCompany().setDetailsURL(uriBuilder.fromPath(prop.getSite().getUrl() + "/v1/companies/{companyId}").buildAndExpand(job.getCompany().getId()).toString());
			job.getCompany().setPhoto(uriBuilder.fromPath(prop.getSite().getUrl() + "/v1/companies/{companyId}/photo").buildAndExpand(job.getCompany().getId()).toString());
		});

		return ResponseEntity.ok(res);
	}

	@Override
	public ResponseEntity<CheckResultDto> checkApplicantApplication(Long applicantId, Long jobOfferId)
			throws WorkstocksBusinessException {
		
		checkForApplication(applicantId, jobOfferId);
		return ResponseEntity
				.ok(new CheckResultDto(applicationService.isJobOfferAppliedForApplicant(jobOfferId, applicantId)));
	}

}
