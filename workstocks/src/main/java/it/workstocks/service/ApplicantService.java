package it.workstocks.service;

import java.util.Set;

import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.user.applicant.ApplicantDto;
import it.workstocks.dto.user.applicant.BasicApplicant;
import it.workstocks.dto.user.applicant.SimpleApplicanDto;
import it.workstocks.dto.user.applicant.cv.CertificationDto;
import it.workstocks.dto.user.applicant.cv.ExperienceDto;
import it.workstocks.dto.user.applicant.cv.QualificationDto;
import it.workstocks.dto.user.applicant.cv.SkillDto;
import it.workstocks.exception.WorkstocksBusinessException;

public interface ApplicantService {
	void checkApplicantExistenceById(Long id) throws WorkstocksBusinessException;
	BasicApplicant findApplicantById(Long id) throws WorkstocksBusinessException;
	ApplicantDto findSimpleApplicantById(Long id) throws WorkstocksBusinessException;	
	void updateApplicantProfile(BasicApplicant applicantDto, Long applicantId) throws WorkstocksBusinessException;
	public void updateApplicantPhoto(Long applicantId, byte[] photo) throws WorkstocksBusinessException;
	public boolean existEmail(String email);
	
	byte[] findApplicantCvById(Long id) throws WorkstocksBusinessException;
	void addApplicantCv(byte[] cv) throws WorkstocksBusinessException;
	
	Set<QualificationDto> findApplicantQualifications(Long id) throws WorkstocksBusinessException;
	Set<ExperienceDto> findApplicantExperiences(Long id) throws WorkstocksBusinessException;
	Set<CertificationDto> findApplicantCertificates(Long id) throws WorkstocksBusinessException;
	Set<SkillDto> findApplicantSkills(Long id) throws WorkstocksBusinessException;
	
	Long addApplicantQualification(QualificationDto qualificationDto) throws WorkstocksBusinessException;
	Long addApplicantCertificate(CertificationDto certificationDto) throws WorkstocksBusinessException;
	Long addApplicantExperience(ExperienceDto experienceDto) throws WorkstocksBusinessException;
	Long addApplicantSkill(SkillDto skillDto) throws WorkstocksBusinessException;
	
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

	PaginatedDtoResponse<SimpleApplicanDto> searchApplicants(PaginatedRequest request);

	long countAllApplicant() throws WorkstocksBusinessException;
}
