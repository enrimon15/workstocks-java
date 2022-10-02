package it.workstocks.rest.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.utility.CheckResultDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.rest.ApplicantFavouritesV1;
import it.workstocks.service.ApplicantService;
import it.workstocks.service.JobOfferService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.validator.QueryParamValidator;

@RestController
public class ApplicantFavouritesV1Impl implements ApplicantFavouritesV1 {
	
	@Autowired
	private ApplicantService applicantService;
	
	@Autowired
	private WorkstocksProperties prop;
	
	@Autowired
	private JobOfferService jobOfferService;
	
	@Autowired
	private QueryParamValidator queryParamValidator;

	
	private UriComponentsBuilder uriBuilder;
	
	@PostConstruct
	private void prepareBaseUri() {
		uriBuilder = UriComponentsBuilder.newInstance().path(prop.getSite().getUrl() + "/v1/applicants/{applicantId}/favourites");
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
	
	@SuppressWarnings("static-access")
	@Override
	public ResponseEntity<PaginatedDtoResponse<SimpleJobOfferDto>> getApplicantFavouritesJobOffers(Long applicantId, Integer page, Integer limit)
			throws WorkstocksBusinessException {
		queryParamValidator.validateInteger("limit", limit, 1, 10);
		checkForApplicant(applicantId);

		PaginatedDtoResponse<SimpleJobOfferDto> jobOffer = jobOfferService.findFavoritesByApplicantId(applicantId, page, limit);
		jobOffer.getElements().stream().forEach(job -> {
			job.setDetailsURL(uriBuilder.cloneBuilder().path("/{jobOfferId}").buildAndExpand(applicantId, job.getId()).toString());
			job.getCompany().setDetailsURL(uriBuilder.fromPath(prop.getSite().getUrl() + "/v1/companies/{companyId}").buildAndExpand(job.getCompany().getId()).toString());
		});

		return ResponseEntity.ok(jobOffer);

	}

	@Override
	public ResponseEntity<Void> addApplicantFavouriteJobOffer(Long applicantId, Long jobOfferId) throws WorkstocksBusinessException {

		checkForApplicant(applicantId);

		return ResponseEntity.created(uriBuilder.cloneBuilder().path("/{jobOfferId}")
						.buildAndExpand(applicantId, jobOfferService.addOrRemoveApplicantFavorite(false, applicantId, jobOfferId))
						.toUri()).build();
	}

	@Override
	public ResponseEntity<Void> deleteApplicantFavouriteJobOffer(Long applicantId, Long jobOfferId)
			throws WorkstocksBusinessException {

		checkForApplication(applicantId, jobOfferId);
		jobOfferService.addOrRemoveApplicantFavorite(true, applicantId, jobOfferId);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<CheckResultDto> checkApplicantFavouriteJobOffer(Long applicantId, Long jobOfferId)
			throws WorkstocksBusinessException {
		checkForApplication(applicantId, jobOfferId);
		CheckResultDto r = new CheckResultDto(jobOfferService.isFavoriteForApplicant(jobOfferId, applicantId));
		return ResponseEntity.ok(r);

	}

}
