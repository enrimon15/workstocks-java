package it.workstocks.service;

import java.util.Set;

import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.exception.WorkstocksBusinessException;


public interface JobOfferService {

	Set<SimpleJobOfferDto> findByCompanyId(Long id, int limit);

	boolean isFavoriteForApplicant(Long jobOfferId, Long applicantId) throws WorkstocksBusinessException;

	Long addOrRemoveApplicantFavorite(boolean isFavorite, Long applicantId, Long jobId) throws WorkstocksBusinessException;

	PaginatedDtoResponse<SimpleJobOfferDto> findFavoritesByApplicantId(Long id, Integer page, Integer pageSize) throws WorkstocksBusinessException;
	
	PaginatedDtoResponse<SimpleJobOfferDto> searchJobOffers(PaginatedRequest request);

	long countAllJobOffers();

	JobOfferDto findById(Long id) throws WorkstocksBusinessException;
	
	Set<JobOfferDto> findAllJobOfferCreatedToday() throws WorkstocksBusinessException;

	void checkExistenceById(Long jobOfferId) throws WorkstocksBusinessException;
	
	
}
