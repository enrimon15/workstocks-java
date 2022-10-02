package it.workstocks.service.impl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dao.IApplicantDao;
import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.user.applicant.SimpleApplicantDto;
import it.workstocks.dto.user.applicant.cv.CertificationDto;
import it.workstocks.dto.user.applicant.cv.ExperienceDto;
import it.workstocks.dto.user.applicant.cv.QualificationDto;
import it.workstocks.dto.user.applicant.cv.SkillDto;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.enums.SkillType;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.entity.user.applicant.curricula.Certification;
import it.workstocks.entity.user.applicant.curricula.Experience;
import it.workstocks.entity.user.applicant.curricula.Qualification;
import it.workstocks.entity.user.applicant.curricula.Skill;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.CertificateRepository;
import it.workstocks.repository.ExperienceRepository;
import it.workstocks.repository.QualificationRepository;
import it.workstocks.repository.SkillRepository;
import it.workstocks.repository.user.ApplicantRepository;
import it.workstocks.service.ApplicantService;
import it.workstocks.utils.AuthUtility;

@Service
@Transactional(readOnly = true)
public class ApplicantServiceImpl implements ApplicantService {
	
	@Autowired
	private EntityMapper mapper;
	
	@Autowired
	private ApplicantRepository applicantRepository;
	
	@Autowired
	private QualificationRepository qualificationRepository;
	
	@Autowired
	private CertificateRepository certificateRepository;

	@Autowired
	private ExperienceRepository experienceRepository;
	
	@Autowired
	private SkillRepository skillRepository;
	
	@Autowired
	private IApplicantDao applicantDao;
	
	private Applicant findOptionalApplicant(Long id) throws WorkstocksBusinessException {
		Optional<Applicant> applicant = applicantRepository.findById(id);
		if (applicant.isPresent()) {
			return applicant.get();
		} else {
			throw new WorkstocksBusinessException("Applicant not found");
		}
	}

	@Override
	public SimpleApplicantDto findApplicantById(Long id) throws WorkstocksBusinessException {
		return mapper.toSimpleDto(findOptionalApplicant(id));
	}
	
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	@Override
	public void updateApplicantProfile(SimpleApplicantDto applicantDto) throws WorkstocksBusinessException {
		Applicant applicant = findOptionalApplicant(applicantDto.getId());
		mapper.updateApplicant(applicantDto, applicant);
		if(applicantDto.getAvatar() != null) applicant.setAvatar(applicantDto.getAvatar());

		applicant = applicantRepository.save(applicant);
		
		AuthUtility.renewAuthenticationPrincipal(applicant);
	}

	@Override
	public Set<QualificationDto> findApplicantQualifications(Long id) throws WorkstocksBusinessException {
		Set<Qualification> qualifications = qualificationRepository.findAllByApplicantIdOrderByStartDateDesc(id);
		return mapper.toDtoQualificationCollection(qualifications);
	}

	@Override
	public Set<ExperienceDto> findApplicantExperiences(Long id) throws WorkstocksBusinessException {
		Set<Experience> experiences = experienceRepository.findAllByApplicantIdOrderByStartDateDesc(id);
		return mapper.toDtoExperienceCollection(experiences);
	}

	@Override
	public Set<CertificationDto> findApplicantCertificates(Long id) throws WorkstocksBusinessException {
		Set<Certification> certificates = certificateRepository.findAllByApplicantIdOrderByDateDesc(id);
		return mapper.toDtoCertificationCollection(certificates);
	}

	@Override
	public Set<SkillDto> findApplicantSkills(Long id) throws WorkstocksBusinessException {
		Set<Skill> skills = skillRepository.findAllByApplicantIdAndSkillType(id, SkillType.APPLICANT);
		return mapper.toDtoSkillCollection(skills);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addApplicantQualification(QualificationDto qualificationDto) throws WorkstocksBusinessException {
		Qualification qualification = mapper.toEntity(qualificationDto);
		Applicant applicant = new Applicant();
		applicant.setId(AuthUtility.getCurrentApplicant().getId());
		qualification.setApplicant(applicant);
		
		qualificationRepository.save(qualification);
	}
	
	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addApplicantCertificate(CertificationDto certificationDto) throws WorkstocksBusinessException {
		Certification certification = mapper.toEntity(certificationDto);
		Applicant applicant = new Applicant();
		applicant.setId(AuthUtility.getCurrentApplicant().getId());
		certification.setApplicant(applicant);
		
		certificateRepository.save(certification);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addApplicantExperience(ExperienceDto experienceDto) throws WorkstocksBusinessException {
		Experience experience = mapper.toEntity(experienceDto);
		Applicant applicant = new Applicant();
		applicant.setId(AuthUtility.getCurrentApplicant().getId());
		experience.setApplicant(applicant);
		
		experienceRepository.save(experience);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addApplicantSkill(SkillDto skillDto) throws WorkstocksBusinessException {
		Skill skill = mapper.toEntity(skillDto);
		Applicant applicant = new Applicant();
		applicant.setId(AuthUtility.getCurrentApplicant().getId());
		skill.setApplicant(applicant);
		skill.setSkillType(SkillType.APPLICANT);
		
		skillRepository.save(skill);
	}
	
	private Qualification getOptionalApplicantQualification(Long id) throws WorkstocksBusinessException {
		Optional<Qualification> q = qualificationRepository.findById(id);
		if (q.isPresent()) {
			Qualification qualification = q.get();
			if (!AuthUtility.compareCurrentUser(qualification.getApplicant().getId())) {
				throw new AccessDeniedException("user unauthorized");
			}
			
			return qualification;
		} else {
			throw new WorkstocksBusinessException("qualification not found");
		}
	}

	@Override
	public QualificationDto findQualificationById(Long id) throws WorkstocksBusinessException {
		return mapper.toDto(getOptionalApplicantQualification(id));
	}
	
	private Experience getOptionalApplicantExperience(Long id) throws WorkstocksBusinessException {
		Optional<Experience> e = experienceRepository.findById(id);
		if (e.isPresent()) {
			Experience experience = e.get();
			if (!AuthUtility.compareCurrentUser(experience.getApplicant().getId())) {
				throw new AccessDeniedException("user unauthorized");
			}
			
			return experience;
		} else {
			throw new WorkstocksBusinessException("experience not found");
		}
	}

	@Override
	public ExperienceDto findExperienceById(Long id) throws WorkstocksBusinessException {
		return mapper.toDto(getOptionalApplicantExperience(id));
	}
	
	private Certification getOptionalApplicantCertification(Long id) throws WorkstocksBusinessException {
		Optional<Certification> c = certificateRepository.findById(id);
		if (c.isPresent()) {
			Certification certification = c.get();
			if (!AuthUtility.compareCurrentUser(certification.getApplicant().getId())) {
				throw new AccessDeniedException("user unauthorized");
			}
			
			return certification;
		} else {
			throw new WorkstocksBusinessException("certification not found");
		}
	}

	@Override
	public CertificationDto findCertificateById(Long id) throws WorkstocksBusinessException {
		return mapper.toDto(getOptionalApplicantCertification(id));
	}
	
	private Skill getOptionalApplicantSkill(Long id) throws WorkstocksBusinessException {
		Optional<Skill> s = skillRepository.findById(id);
		if (s.isPresent()) {
			Skill skill = s.get();
			
			if (skill.getApplicant() == null || !skill.getSkillType().equals(SkillType.APPLICANT)) {
				throw new WorkstocksBusinessException("skill not found");
			}
			
			if (!AuthUtility.compareCurrentUser(skill.getApplicant().getId())) {
				throw new AccessDeniedException("user unauthorized");
			}
			
			return skill;
		} else {
			throw new WorkstocksBusinessException("skill not found");
		}
	}

	@Override
	public SkillDto findSkillById(Long id) throws WorkstocksBusinessException {
		return mapper.toDto(getOptionalApplicantSkill(id));
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void deleteQualificationById(Long id) throws WorkstocksBusinessException {
		Qualification qualification = getOptionalApplicantQualification(id);		
		qualificationRepository.delete(qualification);		
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void deleteExperienceById(Long id) throws WorkstocksBusinessException {
		Experience experience = getOptionalApplicantExperience(id);
		experienceRepository.delete(experience);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void deleteCertificateById(Long id) throws WorkstocksBusinessException {
		Certification certificate = getOptionalApplicantCertification(id);
		certificateRepository.delete(certificate);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void deleteSkillById(Long id) throws WorkstocksBusinessException {
		Skill skill = getOptionalApplicantSkill(id);
		skillRepository.delete(skill);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addApplicantCv(byte[] cv) throws WorkstocksBusinessException {
		Applicant applicant = findOptionalApplicant(AuthUtility.getCurrentApplicant().getId());
		applicant.setCurricula(cv);
		applicant = applicantRepository.save(applicant);
		AuthUtility.getCurrentApplicant().setCurricula(cv);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void modifyQualification(QualificationDto qualificationDto) throws WorkstocksBusinessException {
		Qualification qualification = getOptionalApplicantQualification(qualificationDto.getId());
		
		Qualification qualificationToSave = mapper.toEntity(qualificationDto);
		qualificationToSave.setApplicant(qualification.getApplicant());
		qualificationRepository.save(qualificationToSave);
		
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void modifyExperience(ExperienceDto experienceDto) throws WorkstocksBusinessException {
		Experience experience = getOptionalApplicantExperience(experienceDto.getId());
		
		Experience experienceToSave = mapper.toEntity(experienceDto);
		experienceToSave.setApplicant(experience.getApplicant());
		experienceRepository.save(experienceToSave);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void modifyCertificate(CertificationDto certificationDto) throws WorkstocksBusinessException {
		Certification certificate = getOptionalApplicantCertification(certificationDto.getId());
		
		Certification certificateToSave = mapper.toEntity(certificationDto);
		certificateToSave.setApplicant(certificate.getApplicant());
		certificateRepository.save(certificateToSave);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void modifySkill(SkillDto skillDto) throws WorkstocksBusinessException {
		Skill skill = getOptionalApplicantSkill(skillDto.getId());
		
		Skill skillToSave = mapper.toEntity(skillDto);
		skillToSave.setApplicant(skill.getApplicant());
		skillToSave.setSkillType(skill.getSkillType());
		skillRepository.save(skillToSave);
	}
	
	@Override
	public PaginatedDtoResponse<SimpleApplicantDto> searchApplicants(PaginatedRequest request) {
		PaginatedEntityResponse<Applicant> daoResponse = applicantDao.searchApplicant(request);

		PaginatedDtoResponse<SimpleApplicantDto> dtoReponse = new PaginatedDtoResponse<>();
		
		dtoReponse.setResponse(daoResponse.getReponse());
		dtoReponse.setElements(mapper.toDtoSimpleApplicantCollection(daoResponse.getElements()));
		

		return dtoReponse;
	}
	
	@Override
	public long countAllApplicant() {
		return applicantRepository.count();
	}
	
	@Override
	public Set<SimpleApplicantDto> getAllApplicantsJobAlertingCompany(Long companyId) {		
		return mapper.toDtoSimpleApplicantCollection(applicantRepository.findByJobAlertCompaniesId(companyId));
	}

}
