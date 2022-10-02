package it.workstocks.rest.impl;

import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.auth.PasswordDto;
import it.workstocks.dto.email.EmailDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.FiltersDto;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.user.applicant.BasicApplicant;
import it.workstocks.dto.user.applicant.SimpleApplicanDto;
import it.workstocks.dto.user.applicant.cv.CertificationDto;
import it.workstocks.dto.user.applicant.cv.CurriculumDto;
import it.workstocks.dto.user.applicant.cv.ExperienceDto;
import it.workstocks.dto.user.applicant.cv.QualificationDto;
import it.workstocks.dto.user.applicant.cv.SkillDto;
import it.workstocks.dto.utility.CountResultDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.rest.ApplicantsV1;
import it.workstocks.service.ApplicantService;
import it.workstocks.service.AuthService;
import it.workstocks.service.EmailService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.FileUtils;
import it.workstocks.utils.PdfConverterUtils;
import it.workstocks.utils.Translator;
import it.workstocks.validator.PasswordValidator;
import it.workstocks.validator.QueryParamValidator;

@RestController
public class ApplicantsV1Impl implements ApplicantsV1 {

	@Autowired
	private ApplicantService applicantService;

	@Autowired
	private WorkstocksProperties prop;

	private UriComponentsBuilder uriBuilder;

	@Autowired
	private PdfConverterUtils pdfConverterUtils;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private QueryParamValidator queryParamValidator;
	
	@Autowired
	private Translator translator;
	
	@Autowired
    private PasswordValidator passwordValidator;

	@PostConstruct
	private void prepareBaseUri() {
		uriBuilder = UriComponentsBuilder.newInstance().path(prop.getSite().getUrl() + "/v1/applicants");
	}
	
	private void checkForApplicant(Long applicantId) throws WorkstocksBusinessException {
		applicantService.checkApplicantExistenceById(applicantId);

		if (!AuthUtility.compareCurrentUser(applicantId)) {
			throw new AccessDeniedException(String
					.format("User not authorized to access application for Applicant with id %d", applicantId));
		}
	} 

	@Override
	public ResponseEntity<PaginatedDtoResponse<SimpleApplicanDto>> searchApplicants(String name, String address, String jobTitle, String skill,
			Integer salary, Integer page, Integer limit) throws WorkstocksBusinessException {
		queryParamValidator.validateInteger("limit", limit, 1, 10);
		PaginatedRequest request = buildFilterRequest(name, address, jobTitle, skill, salary, page, limit);
		PaginatedDtoResponse<SimpleApplicanDto> applicants = applicantService.searchApplicants(request);
		
		for (SimpleApplicanDto applicant : applicants.getElements()) {
			applicant.setDetailsURL(uriBuilder.cloneBuilder().path("/{applicantId}").buildAndExpand(applicant.getId()).toString());
			applicant.setPhoto(uriBuilder.cloneBuilder().path("/{applicantId}/photo").buildAndExpand(applicant.getId()).toString());
		}
		
		return ResponseEntity.ok(applicants);
	}

	@Override
	public ResponseEntity<StreamingResponseBody> getApplicantAvatar(Long applicantId) throws WorkstocksBusinessException {
		byte[] avatar = applicantService.findApplicantAvatarById(applicantId);
		if (avatar == null || avatar.length <= 0) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.PHOTO_NOT_FOUND, new Object[] {"applicant",applicantId}), HttpStatus.NOT_FOUND);
		}
		
		String fileExtension = null;
		try {
			fileExtension = FileUtils.getFileExtension(avatar);
		} catch (IOException e) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.FILE_ERROR, null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		StreamingResponseBody res = FileUtils.getStreamingOutput(avatar);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition
				.parse("inline;filename=" + "applicant-" + applicantId + "-photo." + fileExtension));
		
		return ResponseEntity.ok().contentType(FileUtils.getMediaTypeFromImage(fileExtension)).headers(headers).contentLength(avatar.length).body(res);

	}
	
	@Override
	public ResponseEntity<Void> upsertApplicantAvatar(Long applicantId, byte[] photo) throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		if (photo == null) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.PHOTO_REQUIRED, null), HttpStatus.BAD_REQUEST);
		}
		
		if (!FileUtils.checkFileSizeInMB(photo, 10)) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.FILE_TOO_LARGE, new String[] {"photo"}), HttpStatus.PAYLOAD_TOO_LARGE);
		}
		
		boolean isAdding = applicantService.upsertApplicantPhoto(applicantId, photo);
		if (isAdding) {
			return ResponseEntity.created(uriBuilder.cloneBuilder().path("/{applicantId}/photo").buildAndExpand(applicantId).toUri())
					.build();
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<BasicApplicant> getApplicantByapplicantId(Long applicantId)
			throws WorkstocksBusinessException {
		BasicApplicant applicant = applicantService.findApplicantById(applicantId);

		applicant.setQualifications(
				uriBuilder.cloneBuilder().path("/{applicantId}/qualifications").buildAndExpand(applicantId).toString());
		applicant.setSkills(uriBuilder.cloneBuilder().path("/{applicantId}/skills").buildAndExpand(applicantId).toString());
		applicant.setCertifications(
				uriBuilder.cloneBuilder().path("/{applicantId}/certifications").buildAndExpand(applicantId).toString());
		applicant.setExperiences(
				uriBuilder.cloneBuilder().path("/{applicantId}/experiences").buildAndExpand(applicantId).toString());
		applicant.setPhoto(
				uriBuilder.cloneBuilder().path("/{applicantId}/photo").buildAndExpand(applicantId).toString());
		return ResponseEntity.ok(applicant);
	}

	@Override
	public ResponseEntity<Void> updateApplicantByapplicantId(Long applicantId, BasicApplicant applicantDto,
			Errors errors) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"applicant"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}
		
		if(!AuthUtility.compareCurrentUser(applicantId)) {
			throw new AccessDeniedException(String.format("User with id: %d unauthorized to edit profile",applicantId));
		}
		
		applicantService.updateApplicantProfile(applicantDto, applicantId);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("permitAll()")
	@Override
	public ResponseEntity<Set<SkillDto>> getApplicantSkills(Long applicantId) throws WorkstocksBusinessException {
		applicantService.checkApplicantExistenceById(applicantId);
		return ResponseEntity.ok(applicantService.findApplicantSkills(applicantId));

	}

	@Override
	public ResponseEntity<Void> addApplicantSkill(Long applicantId, SkillDto skillDto, Errors errors)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);

		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"skill"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.created(uriBuilder.cloneBuilder().path("/{applicantId}/skills/{skillId}")
				.buildAndExpand(applicantId, applicantService.addApplicantSkill(skillDto)).toUri()).build();
	}

	@Override
	public ResponseEntity<SkillDto> getApplicantSkillById(Long applicantId, Long skillId)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		return ResponseEntity.ok(applicantService.findSkillById(skillId));

	}

	@Override
	public ResponseEntity<Void> deleteApplicantSkillById(Long applicantId, Long skillId)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		applicantService.deleteSkillById(skillId);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> updateApplicantSkill(Long applicantId, Long skillId, SkillDto skillDto, Errors errors)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);

		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"skill"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}

		skillDto.setId(skillId);
		applicantService.modifySkill(skillDto);

		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Set<CertificationDto>> getApplicantCertifications(Long applicantId)
			throws WorkstocksBusinessException {
		applicantService.checkApplicantExistenceById(applicantId);
		return ResponseEntity.ok(applicantService.findApplicantCertificates(applicantId));
	}

	@Override
	public ResponseEntity<Void> addApplicantCertification(Long applicantId, CertificationDto certificationDto,
			Errors errors) throws WorkstocksBusinessException {
		checkForApplicant(applicantId);

		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"certification"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity
				.created(uriBuilder.cloneBuilder().path("/{applicantId}/certifications/{certificationId}")
						.buildAndExpand(applicantId, applicantService.addApplicantCertificate(certificationDto)).toUri())
				.build();
	}

	@Override
	public ResponseEntity<CertificationDto> getApplicantCertificationById(Long applicantId, Long certificationId)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		return ResponseEntity.ok(applicantService.findCertificateById(certificationId));
	}

	@Override
	public ResponseEntity<Void> deleteApplicantCertificationById(Long applicantId, Long certificationId)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		applicantService.deleteCertificateById(certificationId);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> updateApplicantCertification(Long applicantId, Long certificationId,
			CertificationDto certificationDto, Errors errors) throws WorkstocksBusinessException {
		checkForApplicant(applicantId);

		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"certification"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}

		certificationDto.setId(certificationId);
		applicantService.modifyCertificate(certificationDto);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Set<QualificationDto>> getApplicantQualifications(Long applicantId)
			throws WorkstocksBusinessException {
		applicantService.checkApplicantExistenceById(applicantId);
		return ResponseEntity.ok(applicantService.findApplicantQualifications(applicantId));
	}

	@Override
	public ResponseEntity<Void> addApplicantQualification(Long applicantId, QualificationDto qualificationDto,
			Errors errors) throws WorkstocksBusinessException {
		checkForApplicant(applicantId);

		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"qualification"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity
				.created(uriBuilder.cloneBuilder().path("/{applicantId}/qualifications/{qualificationId}")
						.buildAndExpand(applicantId, applicantService.addApplicantQualification(qualificationDto)).toUri())
				.build();
	}

	@Override
	public ResponseEntity<QualificationDto> getApplicantQualificationById(Long applicantId, Long qualificationId)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		return ResponseEntity.ok(applicantService.findQualificationById(qualificationId));
	}

	@Override
	public ResponseEntity<Void> deleteApplicantQualificationById(Long applicantId, Long qualificationId)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		applicantService.deleteQualificationById(qualificationId);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> updateApplicantQualification(Long applicantId, Long qualificationId,
			QualificationDto qualificationDto, Errors errors) throws WorkstocksBusinessException {
		checkForApplicant(applicantId);

		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"qualification"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}

		qualificationDto.setId(qualificationId);
		applicantService.modifyQualification(qualificationDto);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Set<ExperienceDto>> getApplicantExperiences(Long applicantId)
			throws WorkstocksBusinessException {
		applicantService.checkApplicantExistenceById(applicantId);
		return ResponseEntity.ok(applicantService.findApplicantExperiences(applicantId));
	}

	@Override
	public ResponseEntity<Void> addApplicantExperience(Long applicantId, ExperienceDto experienceDto, Errors errors)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		
		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"experience"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.created(uriBuilder.cloneBuilder().path("/{applicantId}/experiences/{experienceId}")
				.buildAndExpand(applicantId, applicantService.addApplicantExperience(experienceDto)).toUri()).build();
	}

	@Override
	public ResponseEntity<ExperienceDto> getApplicantExperienceById(Long applicantId, Long experienceId)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		return ResponseEntity.ok(applicantService.findExperienceById(experienceId));
	}

	@Override
	public ResponseEntity<Void> deleteApplicantExperienceById(Long applicantId, Long experienceId)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);
		applicantService.deleteExperienceById(experienceId);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> updateApplicantExperience(Long applicantId, Long experienceId, ExperienceDto experienceDto,
			Errors errors) throws WorkstocksBusinessException {
		checkForApplicant(applicantId);

		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"experience"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}

		experienceDto.setId(experienceId);
		applicantService.modifyExperience(experienceDto);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<StreamingResponseBody> getApplicantCv(Long applicantId) throws WorkstocksBusinessException {

		byte[] cv = applicantService.findApplicantCvById(applicantId);
		if (cv == null || cv.length <= 0) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.CV_NOT_FOUND, new Long[] {applicantId}), HttpStatus.NOT_FOUND);
		}

		StreamingResponseBody res = FileUtils.getStreamingOutput(cv);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition
				.parse("attachment;filename=" + "CV-applicant-" + applicantId + ".pdf"));

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).headers(headers).contentLength(cv.length).body(res);

	}

	@Override
	public ResponseEntity<Void> upsertApplicantCv(Long applicantId, CurriculumDto curriculumDto, Errors errors)
			throws WorkstocksBusinessException {
		checkForApplicant(applicantId);

		byte[] cv = curriculumDto.isAutogenerate() ? pdfConverterUtils.generateCVPdfFromHtmlTemplate()
				: FileUtils.getByteArrayFromBase64(curriculumDto.getCurriculum());
		
		if (!FileUtils.checkFileSizeInMB(cv, 10)) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.FILE_TOO_LARGE, new String[] {"CV"}), HttpStatus.PAYLOAD_TOO_LARGE);
		}

		boolean isAdding = applicantService.upsertApplicantCv(cv);

		if (isAdding) {
			return ResponseEntity.created(uriBuilder.cloneBuilder().path("/{applicantId}/cv").buildAndExpand(applicantId).toUri())
					.build();
		} else {
			return ResponseEntity.noContent().build();
		}

	}

	@Override
	public ResponseEntity<CountResultDto> countApplicants() throws WorkstocksBusinessException {
		return ResponseEntity.ok(new CountResultDto(applicantService.countAllApplicant()));
	}

	@Override
	public ResponseEntity<Void> sendEmail(EmailDto emailDto, Errors errors) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"email"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}
		
		emailService.sendContactRequest(emailDto.getTo(), AuthUtility.getCurrentUser().getEmail(), emailDto.getMessageBody());
		return ResponseEntity.noContent().build();
	}
	
	private PaginatedRequest buildFilterRequest(String name, String address, String jobTitle, String skill,
			Integer salary, Integer page, Integer limit) {
		PaginatedRequest request = new PaginatedRequest();
		request.setPageNumber(page);
		request.setPageSize(limit);
		FiltersDto filters = new FiltersDto();
		filters.setNameOrSurname(name);
		filters.setAddress(address);
		filters.setJobTitle(jobTitle);
		filters.setSkill(skill);
		filters.setSalary(salary);
		request.setFilters(filters);
		return request;
	}

	@Override
	public ResponseEntity<Void> changePassword(Long applicantId, PasswordDto passwordDto, Errors errors)
			throws WorkstocksBusinessException {
		passwordValidator.validate(passwordDto, errors);

		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"password"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}
		
		checkForApplicant(applicantId);
		
		authService.updatePassword(passwordDto, applicantId);
		return ResponseEntity.noContent().build();
	}

}
