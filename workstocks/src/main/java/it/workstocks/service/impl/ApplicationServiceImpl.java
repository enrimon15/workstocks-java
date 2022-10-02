package it.workstocks.service.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dto.job.JobApplicationDto;
import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.entity.application.JobApplication;
import it.workstocks.entity.application.JobApplicationKey;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.ApplicationRepository;
import it.workstocks.service.ApplicationService;

@Service
@Transactional(readOnly = true)
public class ApplicationServiceImpl implements ApplicationService {
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
	@Autowired
	private EntityMapper mapper;

	@Override
	public long countAllApplication() {
		return applicationRepository.count();
	}
	
	@Override
	public Set<JobOfferDto> retrieveMostPopularJobOffers() {
		Pageable pageable = PageRequest.of(0, 8);
		Page<JobOffer> jobOfferPage = applicationRepository.findPopularJobOffer(pageable);
		return mapper.toDtoJobOfferCollection(new HashSet<>(jobOfferPage.getContent()));
	}
	
	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void removeApplicantApplicationByJob(Long jobId, Long applicantId) throws WorkstocksBusinessException {
		applicationRepository.deleteById(new JobApplicationKey(applicantId, jobId));
	}
	
	private PaginatedDtoResponse<JobApplicationDto> getApplicationsPaginatedByQueryRes(Page<JobApplication> jobApplicationsPaginated) {
		Set<JobApplicationDto> content = mapper.toDtoJobApplicationCollection(new LinkedHashSet<>(jobApplicationsPaginated.getContent()));

		PaginatedResponse paginatedReponse = new PaginatedResponse();
		paginatedReponse.setTotalElements(jobApplicationsPaginated.getTotalElements());
		paginatedReponse.setPageNumber(jobApplicationsPaginated.getPageable().getPageNumber() + 1);
		paginatedReponse.setTotalPages(jobApplicationsPaginated.getTotalPages());
		
		PaginatedDtoResponse<JobApplicationDto> response = new PaginatedDtoResponse<>();
		response.setResponse(paginatedReponse);
		response.setElements(content);

		return response;
	}

	@Override
	public PaginatedDtoResponse<JobApplicationDto> findApplicationsByApplicantId(Long id, Integer pageNumber) {
		Pageable pageable = PageRequest.of(pageNumber - 1, 10);
		Page<JobApplication> jobApplicationPaginated = applicationRepository.findByApplicantIdOrderByCreatedAtDesc(id, pageable);
		return getApplicationsPaginatedByQueryRes(jobApplicationPaginated);
	}

	@Override
	public boolean isJobOfferAppliedForApplicant(Long jobOfferId, Long applicantId) {
		return applicationRepository.existsById(new JobApplicationKey(applicantId, jobOfferId));
	}
	
	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void applyForApplicant(Long jobId, Long applicantId) throws WorkstocksBusinessException {
		if (isJobOfferAppliedForApplicant(jobId, applicantId)) {
			throw new WorkstocksBusinessException("Job Offer already applied");
		}
		
		JobApplication application = new JobApplication();
		application.setId(new JobApplicationKey(applicantId, jobId));
		application.setApplicantFromDto(applicantId);
		application.setJobOfferFromDto(jobId);
		applicationRepository.save(application);
	}
	
	@Override
	public PaginatedDtoResponse<JobApplicationDto> findCandidatesByJobId(Long jobId, Integer pageNumber) throws WorkstocksBusinessException {
		Pageable pageable = PageRequest.of(pageNumber - 1, 10);
		Page<JobApplication> applicationsPaginated = applicationRepository.findByJobOfferIdOrderByCreatedAtDesc(jobId, pageable);
		return getApplicationsPaginatedByQueryRes(applicationsPaginated);
	}

	@Override
	public Integer countApplicationsByJobId(Long jobId) throws WorkstocksBusinessException {
		return applicationRepository.countByJobOfferId(jobId);
	}


}
