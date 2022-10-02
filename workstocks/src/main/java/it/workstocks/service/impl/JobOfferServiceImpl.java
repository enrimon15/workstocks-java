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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dao.IJobOfferDao;
import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.enums.SkillType;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.entity.user.applicant.curricula.Skill;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.ApplicationRepository;
import it.workstocks.repository.JobOfferRepository;
import it.workstocks.repository.user.ApplicantRepository;
import it.workstocks.security.Roles;
import it.workstocks.service.JobOfferService;
import it.workstocks.utils.AuthUtility;

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
	private ApplicationRepository applicationRepository;

	@Autowired
	private IJobOfferDao jobOfferDao;
	
	private JobOffer findOptionalJobOffer(Long idJob, boolean checkOwnerPermission) throws WorkstocksBusinessException {
		Optional<JobOffer> jo = jobRepository.findById(idJob);

		if (jo.isPresent()) {
			if (checkOwnerPermission && AuthUtility.hasRole(Roles.COMPANY_OWNER) && !AuthUtility.getCurrentCompanyOwner().getCompany().getId().equals(jo.get().getCompany().getId())) {
				throw new AccessDeniedException("User unauthorized to get job offer");
			}
			return jo.get();
		} else {
			String err = String.format("Job offer not found for id %d", idJob);
			throw new WorkstocksBusinessException(err);
		}
	}
	
	@Override
	public JobOfferDto findById(Long id) throws WorkstocksBusinessException {
		return mapper.toDto(findOptionalJobOffer(id, false));
	}
	
	private PaginatedDtoResponse<JobOfferDto> getJobOfferPaginatedByQueryRes(Page<JobOffer> jobsPaginated) {
		Set<JobOfferDto> content = mapper.toDtoJobOfferCollection(new LinkedHashSet<>(jobsPaginated.getContent()));

		PaginatedResponse paginatedReponse = new PaginatedResponse();
		paginatedReponse.setTotalElements(jobsPaginated.getTotalElements());
		paginatedReponse.setPageNumber(jobsPaginated.getPageable().getPageNumber() + 1);
		paginatedReponse.setTotalPages(jobsPaginated.getTotalPages());
		
		PaginatedDtoResponse<JobOfferDto> response = new PaginatedDtoResponse<>();
		response.setResponse(paginatedReponse);
		response.setElements(content);

		return response;
	}

	@Override
	public PaginatedDtoResponse<JobOfferDto> findByCompanyId(Long id, int pageNumber) throws WorkstocksBusinessException {
		Pageable pageable = PageRequest.of(pageNumber - 1, 10);
		Page<JobOffer> jobsPaginated = jobRepository.findByCompanyIdOrderByCreatedAtDesc(id, pageable);
		return getJobOfferPaginatedByQueryRes(jobsPaginated);
	}
	
	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void insertOrUpdateJobOffer(JobOfferDto jobOfferDto) throws WorkstocksBusinessException {
		JobOffer jobOffer = null;
		if (jobOfferDto.getId() != null) {
			jobOffer = findOptionalJobOffer(jobOfferDto.getId(), true);
			mapper.updateJobOfferEntity(jobOfferDto, jobOffer);
		} else {
			jobOffer = mapper.toEntity(jobOfferDto);
			Company comp = new Company();
			comp.setId(AuthUtility.getCurrentCompanyOwner().getCompany().getId());
			jobOffer.setCompany(comp);
		}

		if (jobOffer.getSkills() != null) {
			jobOffer.getSkills().clear();
		} else {
			jobOffer.setSkills(new LinkedHashSet<>());
		}

		for (String skillName : jobOfferDto.getSkillFromFE()) {
			Skill skill = new Skill();
			skill.setName(skillName);
			skill.setSkillType(SkillType.JOB_OFFER);
			jobOffer.getSkills().add(skill);
		}

		jobRepository.save(jobOffer);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void deleteById(Long id) throws WorkstocksBusinessException {
		JobOffer job = findOptionalJobOffer(id, true);
		applicationRepository.deleteByJobOfferId(id);	
		jobRepository.delete(job);
	}

	@Override
	public Set<JobOfferDto> findLatestsByCompanyId(Long companyId, int jobsNumber) throws WorkstocksBusinessException {
		Set<JobOffer> latestJobs = new LinkedHashSet<>(
				jobRepository.findByCompanyIdAndDueDateGreaterThanEqualOrderByDueDateDesc(companyId, LocalDate.now(),
						PageRequest.of(0, jobsNumber)));
		return mapper.toDtoJobOfferCollection(latestJobs);
	}

	@Override
	public boolean isFavoriteForApplicant(Long jobOfferId, Long applicantId) throws WorkstocksBusinessException {
		return applicantRepository.existsByIdAndFavouritesJobId(applicantId, jobOfferId);
	}

	
	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addOrRemoveApplicantFavorite(boolean isFavorite, Long applicantId, Long jobId) throws WorkstocksBusinessException {
		JobOffer job = findOptionalJobOffer(jobId, false);

		if (isFavorite) {
			job.getFavouritesApplicant().removeIf(applicant -> applicant.getId().equals(applicantId));
		} else {
			Applicant app = new Applicant();
			app.setId(applicantId);
			job.getFavouritesApplicant().add(app);
		}

		jobRepository.save(job);
	}


	@Override
	public PaginatedDtoResponse<JobOfferDto> findFavoritesByApplicantId(Long id, Integer page) throws WorkstocksBusinessException {
		Pageable pageable = PageRequest.of(page - 1, 10);
		Page<JobOffer> jobsPaginated = jobRepository.findByFavouritesApplicantIdOrderByDueDateDesc(id, pageable);
		return getJobOfferPaginatedByQueryRes(jobsPaginated);
	}

	@Override
	public PaginatedDtoResponse<JobOfferDto> searchJobOffers(PaginatedRequest request) throws WorkstocksBusinessException {
		PaginatedEntityResponse<JobOffer> daoResponse = jobOfferDao.searchJobOffer(request);

		PaginatedDtoResponse<JobOfferDto> dtoResponse = new PaginatedDtoResponse<>();

		dtoResponse.setElements(mapper.toDtoJobOfferCollection(daoResponse.getElements()));
		dtoResponse.setResponse(daoResponse.getReponse());

		return dtoResponse;
	}
	
	@Override
	public long countAllJobOffers() throws WorkstocksBusinessException {
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
