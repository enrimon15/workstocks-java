package it.workstocks.service.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.entity.application.JobApplication;
import it.workstocks.entity.application.JobApplicationKey;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.queue.QueueProducer;
import it.workstocks.queue.message.EmailMessageJob;
import it.workstocks.queue.message.EmailType;
import it.workstocks.repository.ApplicationRepository;
import it.workstocks.service.ApplicationService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;

@Service
@Transactional(readOnly = true)
public class ApplicationServiceImpl implements ApplicationService {
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
	@Autowired
	private EntityMapper mapper;
	
	@Autowired
	private QueueProducer producer;
	
	@Autowired
	private Translator translator;

	@Override
	public long countAllApplication() {
		return applicationRepository.count();
	}
	
	@Override
	public Set<SimpleJobOfferDto> retrieveMostPopularJobOffers(Integer limit) {
		Pageable pageable = PageRequest.of(0, limit);
		Page<JobOffer> jobOfferPage = applicationRepository.findPopularJobOffer(pageable);
		return mapper.toDtoJobOfferCollection(new HashSet<>(jobOfferPage.getContent()));
	}
	
	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void removeApplicantApplicationByJob(Long jobId, Long applicantId) throws WorkstocksBusinessException {
		applicationRepository.deleteById(new JobApplicationKey(applicantId, jobId));
	}
	
	private PaginatedDtoResponse<SimpleJobOfferDto> getApplicationsPaginatedByQueryRes(Page<JobApplication> jobApplicationsPaginated) {
		Set<SimpleJobOfferDto> content = new LinkedHashSet<>();
		for (JobApplication job : jobApplicationsPaginated) {
			content.add(mapper.toSimpleDto(job.getJobOffer()));
		}

		PaginatedResponse paginatedReponse = new PaginatedResponse();
		paginatedReponse.setTotalElements(jobApplicationsPaginated.getTotalElements());
		paginatedReponse.setPageNumber(jobApplicationsPaginated.getPageable().getPageNumber() + 1);
		paginatedReponse.setTotalPages(jobApplicationsPaginated.getTotalPages());
		paginatedReponse.setPageSize(jobApplicationsPaginated.getNumberOfElements());
		
		PaginatedDtoResponse<SimpleJobOfferDto> response = new PaginatedDtoResponse<>();
		response.setResponse(paginatedReponse);
		response.setElements(content);

		return response;
	}

	@Override
	public PaginatedDtoResponse<SimpleJobOfferDto> findApplicationsByApplicantId(Long id, Integer pageNumber, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
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
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.JOB_ALREADY_APPLIED, new Long[] {jobId}), HttpStatus.CONFLICT);
		}
		
		JobApplication application = new JobApplication();
		application.setId(new JobApplicationKey(applicantId, jobId));
		application.setApplicantFromDto(applicantId);
		application.setJobOfferFromDto(jobId);
		applicationRepository.save(application);
		
		EmailMessageJob m = new EmailMessageJob();
		m.setEmailType(EmailType.APPLICATION);
		m.setApplicantId(AuthUtility.getCurrentApplicant().getId());
		m.setJobOfferId(jobId);
		producer.send(m);
	}

	@Override
	public Integer countApplicationsByJobId(Long jobId) throws WorkstocksBusinessException {
		return applicationRepository.countByJobOfferId(jobId);
	}


}
