package it.workstocks.service;

import java.util.Set;

import it.workstocks.dto.job.JobApplicationDto;
import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.exception.WorkstocksBusinessException;

public interface ApplicationService {
	long countAllApplication() throws WorkstocksBusinessException;
	
	Set<JobOfferDto> retrieveMostPopularJobOffers() throws WorkstocksBusinessException;
	
	void applyForApplicant(Long jobId, Long applicantId) throws WorkstocksBusinessException;
	
	void removeApplicantApplicationByJob(Long jobId, Long applicantId) throws WorkstocksBusinessException;
	
	PaginatedDtoResponse<JobApplicationDto> findApplicationsByApplicantId(Long id, Integer pageNumber) throws WorkstocksBusinessException;
	
	boolean isJobOfferAppliedForApplicant(Long jobOfferId, Long applicantId) throws WorkstocksBusinessException;
	
	PaginatedDtoResponse<JobApplicationDto> findCandidatesByJobId(Long jobId, Integer pageNumber) throws WorkstocksBusinessException;

	Integer countApplicationsByJobId(Long jobId) throws WorkstocksBusinessException;
}
