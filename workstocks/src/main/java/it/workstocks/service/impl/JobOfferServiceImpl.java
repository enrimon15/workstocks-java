package it.workstocks.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.job.SimpleJobOfferDto;
import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.ApplicantRepository;
import it.workstocks.repository.JobOfferRepository;
import it.workstocks.repository.custom.IJobOfferDao;
import it.workstocks.service.JobOfferService;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;

@Service
@Transactional(readOnly = true)
public class JobOfferServiceImpl implements JobOfferService {

	@Autowired
	private EntityMapper mapper;

	@Autowired
	private JobOfferRepository jobRepository;

	@Autowired
	private ApplicantRepository applicantRepository;

	@Autowired
	private IJobOfferDao jobOfferDao;
	
	@Autowired
	private Translator translator;
	
	private JobOffer findOptionalJobOffer(Long idJob, boolean checkOwnerPermission) throws WorkstocksBusinessException {
		Optional<JobOffer> jo = jobRepository.findById(idJob);
		if (jo.isPresent()) {
			return jo.get();
		} else {			
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.NOT_FOUND, new Object[] {"job offer", idJob}),HttpStatus.NOT_FOUND);
		}
	}
	
	@Override
	public void checkExistenceById(Long jobOfferId) throws WorkstocksBusinessException {
		boolean exist = jobRepository.existsById(jobOfferId);
		if (!exist) {			
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.NOT_FOUND, new Object[] {"job offer", jobOfferId}),HttpStatus.NOT_FOUND);
		}
	}
	
	@Override
	public JobOfferDto findById(Long id) throws WorkstocksBusinessException {
		return mapper.toDto(findOptionalJobOffer(id, false));
	}
	
	private PaginatedDtoResponse<SimpleJobOfferDto> getJobOfferPaginatedByQueryRes(Page<JobOffer> jobsPaginated) {
		Set<SimpleJobOfferDto> content = mapper.toDtoJobOfferCollection(new LinkedHashSet<>(jobsPaginated.getContent()));

		PaginatedResponse paginatedReponse = new PaginatedResponse();
		paginatedReponse.setTotalElements(jobsPaginated.getTotalElements());
		paginatedReponse.setPageNumber(jobsPaginated.getPageable().getPageNumber() + 1);
		paginatedReponse.setTotalPages(jobsPaginated.getTotalPages());
		paginatedReponse.setPageSize(jobsPaginated.getNumberOfElements());
		
		PaginatedDtoResponse<SimpleJobOfferDto> response = new PaginatedDtoResponse<>();
		response.setResponse(paginatedReponse);
		response.setElements(content);

		return response;
	}

	@Override
	public Set<SimpleJobOfferDto> findByCompanyId(Long id, int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		Set<JobOffer> companyJobs = new HashSet<>(jobRepository.findByCompanyIdAndDueDateGreaterThanEqualOrderByDueDateDesc(id, LocalDate.now(), pageable).getContent());
		return mapper.toDtoJobOfferCollection(companyJobs);
	}

	@Override
	public boolean isFavoriteForApplicant(Long jobOfferId, Long applicantId) throws WorkstocksBusinessException {
		return applicantRepository.existsByIdAndFavouritesJobId(applicantId, jobOfferId);
	}

	
	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public Long addOrRemoveApplicantFavorite(boolean isFavorite, Long applicantId, Long jobId) throws WorkstocksBusinessException {
		JobOffer job = findOptionalJobOffer(jobId, false);

		if (isFavorite) {
			boolean isRemoved = job.getFavouritesApplicant().removeIf(applicant -> applicant.getId().equals(applicantId));
			if (!isRemoved) {
				throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.FAVOURITE_NOT_FOUND, new Object[] {jobId, applicantId}),HttpStatus.NOT_FOUND);
			}
		} else {
			if (isFavoriteForApplicant(jobId, applicantId)) {
				throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.FAVOURITE_ALREADY_APPLIED, new Object[] {jobId, applicantId}),HttpStatus.CONFLICT);
			}
			
			Applicant app = new Applicant();
			app.setId(applicantId);
			job.getFavouritesApplicant().add(app);
		}

		jobRepository.save(job);
		
		return jobId;
	}


	@Override
	public PaginatedDtoResponse<SimpleJobOfferDto> findFavoritesByApplicantId(Long id, Integer page, Integer pageSize) throws WorkstocksBusinessException {
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		Page<JobOffer> jobsPaginated = jobRepository.findByFavouritesApplicantIdOrderByDueDateDesc(id, pageable);
		return getJobOfferPaginatedByQueryRes(jobsPaginated);
	}

	@Override
	public PaginatedDtoResponse<SimpleJobOfferDto> searchJobOffers(PaginatedRequest request) {
		PaginatedEntityResponse<JobOffer> daoResponse = jobOfferDao.searchJobOffer(request);

		PaginatedDtoResponse<SimpleJobOfferDto> dtoResponse = new PaginatedDtoResponse<>();

		dtoResponse.setElements(mapper.toDtoJobOfferCollection(daoResponse.getElements()));
		dtoResponse.setResponse(daoResponse.getReponse());

		return dtoResponse;
	}
	
	@Override
	public long countAllJobOffers() {
		return jobRepository.count();
	}
	
	@Override
	public Set<JobOfferDto> findAllJobOfferCreatedToday() throws WorkstocksBusinessException {
		Set<JobOfferDto> res = new HashSet<>();
		
		jobRepository.findByCreatedAtGreaterThanEqual(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT))
		.stream()
		.map(el -> mapper.toDto(el))
		.forEach(res::add);
		
		return res;
	}

}
