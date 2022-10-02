package it.workstocks.service;

import java.util.Set;

import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.exception.WorkstocksBusinessException;

public interface ApplicationService {
	long countAllApplication();
	
	Set<SimpleJobOfferDto> retrieveMostPopularJobOffers(Integer limit);
	
	void applyForApplicant(Long jobId, Long applicantId) throws WorkstocksBusinessException;
	
	void removeApplicantApplicationByJob(Long jobId, Long applicantId) throws WorkstocksBusinessException;
	
	PaginatedDtoResponse<SimpleJobOfferDto> findApplicationsByApplicantId(Long id, Integer pageNumber, Integer pageSize) throws WorkstocksBusinessException;
	
	boolean isJobOfferAppliedForApplicant(Long jobOfferId, Long applicantId) throws WorkstocksBusinessException;

	Integer countApplicationsByJobId(Long jobId) throws WorkstocksBusinessException;
}
