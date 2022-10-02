package it.workstocks.service;

import java.util.Set;

import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.exception.WorkstocksBusinessException;


public interface JobOfferService {

	PaginatedDtoResponse<JobOfferDto> findByCompanyId(Long id, int page) throws WorkstocksBusinessException;
	
	void insertOrUpdateJobOffer(JobOfferDto jobOfferDto) throws WorkstocksBusinessException;

	void deleteById(Long id) throws WorkstocksBusinessException;
	
	Set<JobOfferDto> findLatestsByCompanyId(Long companyId, int jobsNumber) throws WorkstocksBusinessException;

	boolean isFavoriteForApplicant(Long jobOfferId, Long applicantId) throws WorkstocksBusinessException;

	void addOrRemoveApplicantFavorite(boolean isFavorite, Long applicantId, Long jobId) throws WorkstocksBusinessException;

	PaginatedDtoResponse<JobOfferDto> findFavoritesByApplicantId(Long id, Integer page) throws WorkstocksBusinessException;
	
	PaginatedDtoResponse<JobOfferDto> searchJobOffers(PaginatedRequest request) throws WorkstocksBusinessException;

	long countAllJobOffers() throws WorkstocksBusinessException;

	JobOfferDto findById(Long id) throws WorkstocksBusinessException;
	
	Set<JobOfferDto> findAllJobOfferCreatedToday() throws WorkstocksBusinessException;
	
	
}
