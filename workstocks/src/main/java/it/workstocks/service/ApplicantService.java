package it.workstocks.service;

import java.util.Set;

import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.user.applicant.SimpleApplicantDto;
import it.workstocks.dto.user.applicant.cv.CertificationDto;
import it.workstocks.dto.user.applicant.cv.ExperienceDto;
import it.workstocks.dto.user.applicant.cv.QualificationDto;
import it.workstocks.dto.user.applicant.cv.SkillDto;
import it.workstocks.exception.WorkstocksBusinessException;

public interface ApplicantService {
	SimpleApplicantDto findApplicantById(Long id) throws WorkstocksBusinessException;
	void updateApplicantProfile(SimpleApplicantDto applicantDto) throws WorkstocksBusinessException;
	
	Set<QualificationDto> findApplicantQualifications(Long id) throws WorkstocksBusinessException;
	Set<ExperienceDto> findApplicantExperiences(Long id) throws WorkstocksBusinessException;
	Set<CertificationDto> findApplicantCertificates(Long id) throws WorkstocksBusinessException;
	Set<SkillDto> findApplicantSkills(Long id) throws WorkstocksBusinessException;
	
	void addApplicantQualification(QualificationDto qualificationDto) throws WorkstocksBusinessException;
	void addApplicantCertificate(CertificationDto certificationDto) throws WorkstocksBusinessException;
	void addApplicantExperience(ExperienceDto experienceDto) throws WorkstocksBusinessException;
	void addApplicantSkill(SkillDto skillDto) throws WorkstocksBusinessException;
	void addApplicantCv(byte[] cv) throws WorkstocksBusinessException;
	
	QualificationDto findQualificationById(Long id) throws WorkstocksBusinessException;
	ExperienceDto findExperienceById(Long id) throws WorkstocksBusinessException;
	CertificationDto findCertificateById(Long id) throws WorkstocksBusinessException;
	SkillDto findSkillById(Long id) throws WorkstocksBusinessException;
	
	void deleteQualificationById(Long id) throws WorkstocksBusinessException;
	void deleteExperienceById(Long id) throws WorkstocksBusinessException;
	void deleteCertificateById(Long id) throws WorkstocksBusinessException;
	void deleteSkillById(Long id) throws WorkstocksBusinessException;

	void modifyQualification(QualificationDto qualificationDto) throws WorkstocksBusinessException;
	void modifyExperience(ExperienceDto experienceDto) throws WorkstocksBusinessException;
	void modifyCertificate(CertificationDto certificationDto) throws WorkstocksBusinessException;
	void modifySkill(SkillDto skillDto) throws WorkstocksBusinessException;

	PaginatedDtoResponse<SimpleApplicantDto> searchApplicants(PaginatedRequest request) throws WorkstocksBusinessException;

	long countAllApplicant() throws WorkstocksBusinessException;
	
	Set<SimpleApplicantDto> getAllApplicantsJobAlertingCompany(Long companyId);
}
