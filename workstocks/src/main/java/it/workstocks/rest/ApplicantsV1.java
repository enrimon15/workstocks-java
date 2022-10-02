package it.workstocks.rest;

import static it.workstocks.utils.AuthUtility.IS_APPLICANT;
import static it.workstocks.utils.AuthUtility.PERMIT_ALL;

import java.util.Set;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.workstocks.dto.auth.PasswordDto;
import it.workstocks.dto.email.EmailDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.user.applicant.BasicApplicant;
import it.workstocks.dto.user.applicant.SimpleApplicanDto;
import it.workstocks.dto.user.applicant.cv.CertificationDto;
import it.workstocks.dto.user.applicant.cv.CurriculumDto;
import it.workstocks.dto.user.applicant.cv.ExperienceDto;
import it.workstocks.dto.user.applicant.cv.QualificationDto;
import it.workstocks.dto.user.applicant.cv.SkillDto;
import it.workstocks.dto.utility.CheckResultDto;
import it.workstocks.dto.utility.CountResultDto;
import it.workstocks.exception.WorkstocksBusinessException;

@RequestMapping("v1/applicants")
public interface ApplicantsV1 {

	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "APPLICANT")
	@Operation(summary = "Get list of filtered and paginated applicants (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameter"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "search", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PaginatedDtoResponse<SimpleApplicanDto>> searchApplicants(@RequestParam(required = false) String name,
			@RequestParam(required = false) String address,
			@RequestParam(name = "job-title", required = false) String jobTitle,
			@RequestParam(required = false) String skill,
			@RequestParam(required = false) Integer salary,
			@RequestParam Integer page,
			@RequestParam Integer limit) throws WorkstocksBusinessException;

	/*
	 * APPLICANT
	 */
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "APPLICANT")
	@Operation(summary = "Get applicant by id (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<BasicApplicant> getApplicantByapplicantId(@PathVariable("applicantId") Long applicantId)
			throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT")
	@Operation(summary = "Partially update applicant by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong applicant payload"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PatchMapping(path = "{applicantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> updateApplicantByapplicantId(@PathVariable("applicantId") Long applicantId,
			@Valid @RequestBody BasicApplicant applicantDto, Errors errors) throws WorkstocksBusinessException;

	/*
	 * AVATAR
	 */
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "APPLICANT")
	@Operation(summary = "Get applicant photo (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or photo not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}/photo", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
	ResponseEntity<byte[]> getApplicantAvatar(@PathVariable("applicantId") Long applicantId)
			throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT")
	@Operation(summary = "Update applicant photo")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong photo payload"),
		@ApiResponse(responseCode = "413", description = "Payload too large"),
		@ApiResponse(responseCode = "403", description = "Access denied to update photo"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PutMapping(path = "{applicantId}/photo", consumes = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
	ResponseEntity<Void> updateApplicantAvatar(@PathVariable("applicantId") Long applicantId, @RequestBody byte[] photo)
			throws WorkstocksBusinessException;

	/*
	 * SKILLS
	 */
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "APPLICANT SKILLS")
	@Operation(summary = "Get applicant skills (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}/skills", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Set<SkillDto>> getApplicantSkills(@PathVariable("applicantId") Long applicantId)
			throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT SKILLS")
	@Operation(summary = "Add applicant skill")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong skill payload"),
		@ApiResponse(responseCode = "403", description = "Access denied to add skill"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "/{applicantId}/skills", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addApplicantSkill(@PathVariable("applicantId") Long applicantId,
			@Valid @RequestBody SkillDto skillDto, Errors errors) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT SKILLS")
	@Operation(summary = "Get skill by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Skill or applicant not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to get skill"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}/skills/{skillId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<SkillDto> getApplicantSkillById(@PathVariable("applicantId") Long applicantId,
			@PathVariable("skillId") Long skillId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT SKILLS")
	@Operation(summary = "Delete skill by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Skill or applicant not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to delete skill"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@DeleteMapping("{applicantId}/skills/{skillId}")
	ResponseEntity<Void> deleteApplicantSkillById(@PathVariable("applicantId") Long applicantId,
			@PathVariable("skillId") Long skillId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT SKILLS")
	@Operation(summary = "Update skill by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Skill or applicant not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong skill payload"),
		@ApiResponse(responseCode = "403", description = "Access denied to get skill"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PutMapping(path = "{applicantId}/skills/{skillId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> updateApplicantSkill(@PathVariable("applicantId") Long applicantId,
			@PathVariable("skillId") Long skillId, @Valid @RequestBody SkillDto skillDto, Errors errors)
			throws WorkstocksBusinessException;

	/*
	 * CERITIFICATIONS
	 */
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "APPLICANT CERTIFICATIONS")
	@Operation(summary = "Get applicant certifications (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}/certifications", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Set<CertificationDto>> getApplicantCertifications(@PathVariable("applicantId") Long applicantId)
			throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT CERTIFICATIONS")
	@Operation(summary = "Add applicant certification")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong certification payload"),
		@ApiResponse(responseCode = "403", description = "Access denied to add certification"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "{applicantId}/certifications", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addApplicantCertification(@PathVariable("applicantId") Long applicantId,
			@Valid @RequestBody CertificationDto certificationDto, Errors errors) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT CERTIFICATIONS")
	@Operation(summary = "Get applicant certification by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or certification not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to add certification"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}/certifications/{certificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CertificationDto> getApplicantCertificationById(@PathVariable("applicantId") Long applicantId,
			@PathVariable("certificationId") Long certificationId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT CERTIFICATIONS")
	@Operation(summary = "Delete applicant certification by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or certification not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to delete certification"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@DeleteMapping("{applicantId}/certifications/{certificationId}")
	ResponseEntity<Void> deleteApplicantCertificationById(@PathVariable("applicantId") Long applicantId,
			@PathVariable("certificationId") Long certificationId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT CERTIFICATIONS")
	@Operation(summary = "Update applicant certification by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or certification not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong cartification payload"),
		@ApiResponse(responseCode = "403", description = "Access denied to update certification"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PutMapping(path = "{applicantId}/certifications/{certificationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> updateApplicantCertification(@PathVariable("applicantId") Long applicantId,
			@PathVariable("certificationId") Long certificationId,
			@Valid @RequestBody CertificationDto certificationDto, Errors errors) throws WorkstocksBusinessException;

	/*
	 * QUALIFICATIONS
	 */
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "APPLICANT QUALIFICATIONS")
	@Operation(summary = "Get applicant certifications (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}/qualifications", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Set<QualificationDto>> getApplicantQualifications(@PathVariable("applicantId") Long applicantId)
			throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT QUALIFICATIONS")
	@Operation(summary = "Add applicant qualification")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong qualification payload"),
		@ApiResponse(responseCode = "403", description = "Access denied to add qualification"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "{applicantId}/qualifications", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addApplicantQualification(@PathVariable("applicantId") Long applicantId,
			@Valid @RequestBody QualificationDto qualificationDto, Errors errors) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT QUALIFICATIONS")
	@Operation(summary = "Get applicant qualification by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or qualification not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to get qualification"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}/qualifications/{qualificationId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<QualificationDto> getApplicantQualificationById(@PathVariable("applicantId") Long applicantId,
			@PathVariable("qualificationId") Long qualificationId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT QUALIFICATIONS")
	@Operation(summary = "Delete applicant qualification by idd")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or qualification not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to delete qualification"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@DeleteMapping("{applicantId}/qualifications/{qualificationId}")
	ResponseEntity<Void> deleteApplicantQualificationById(@PathVariable("applicantId") Long applicantId,
			@PathVariable("qualificationId") Long qualificationId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT QUALIFICATIONS")
	@Operation(summary = "Update applicant certification by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or qualification not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong qualification payload"),
		@ApiResponse(responseCode = "403", description = "Access denied to update qualification"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PutMapping(path = "{applicantId}/qualifications/{qualificationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> updateApplicantQualification(@PathVariable("applicantId") Long applicantId,
			@PathVariable("qualificationId") Long qualificationId,
			@Valid @RequestBody QualificationDto qualificationDto, Errors errors) throws WorkstocksBusinessException;

	/*
	 * EXPERIENCES
	 */
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "APPLICANT EXPERIENCES")
	@Operation(summary = "Get applicant experiences (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}/experiences", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Set<ExperienceDto>> getApplicantExperiences(@PathVariable("applicantId") Long applicantId)
			throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT EXPERIENCES")
	@Operation(summary = "Add applicant experience")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong experience payload"),
		@ApiResponse(responseCode = "403", description = "Access denied to add experience"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "{applicantId}/experiences", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addApplicantExperience(@PathVariable("applicantId") Long applicantId,
			@Valid @RequestBody ExperienceDto experienceDto, Errors errors) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT EXPERIENCES")
	@Operation(summary = "Get applicant experience by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or experience not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to get experience"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}/experiences/{experienceId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<ExperienceDto> getApplicantExperienceById(@PathVariable("applicantId") Long applicantId,
			@PathVariable("experienceId") Long experienceId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT EXPERIENCES")
	@Operation(summary = "Delete applicant experience by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or experience not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to delete experience"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@DeleteMapping("{applicantId}/experiences/{experienceId}")
	ResponseEntity<Void> deleteApplicantExperienceById(@PathVariable("applicantId") Long applicantId,
			@PathVariable("experienceId") Long experienceId) throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT EXPERIENCES")
	@Operation(summary = "Update applicant experience by id")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or experience not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong experience payload"),
		@ApiResponse(responseCode = "403", description = "Access denied to update experience"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PutMapping(path = "{applicantId}/experiences/{experienceId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> updateApplicantExperience(@PathVariable("applicantId") Long applicantId,
			@PathVariable("experienceId") Long experienceId, @Valid @RequestBody ExperienceDto experienceDto,
			Errors errors) throws WorkstocksBusinessException;

	/*
	 * CV
	 */
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "APPLICANT CURRICULUM")
	@Operation(summary = "Get applicant cv (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant or cv not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "{applicantId}/cv", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	ResponseEntity<StreamingResponseBody> getApplicantCv(@PathVariable("applicantId") Long applicantId)
			throws WorkstocksBusinessException;

	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT CURRICULUM")
	@Operation(summary = "Add applicant cv")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "Applicant not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to add cv"),
		@ApiResponse(responseCode = "413", description = "CV payload is too large"),
		@ApiResponse(responseCode = "400", description = "Wrong cv payload"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PutMapping(path = "{applicantId}/cv", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addApplicantCv(@PathVariable("applicantId") Long applicantId,
			@Valid @RequestBody CurriculumDto curriculumDto, Errors errors) throws WorkstocksBusinessException;
	

	/*
	 * COUNT
	 */
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "APPLICANT")
	@Operation(summary = "Count total number of applicants (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "count", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CountResultDto> countApplicants()
			throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)	
	@Tag(name = "UTILITY")
	@Operation(summary = "Send email to a company")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Wrong email payload"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PostMapping(path = "email", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> sendEmail(@Valid @RequestBody EmailDto emailDto, Errors errors) throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "APPLICANT")
	@Operation(summary = "Change user password")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Wrong password payload"),
		@ApiResponse(responseCode = "404", description = "Applicant not found not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to change password"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@PutMapping(path = "{applicantId}/password", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> changePassword(@PathVariable Long applicantId, @RequestBody @Valid PasswordDto passwordDto, Errors errors) throws WorkstocksBusinessException;

	

	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "APPLICANT")
	@Operation(summary = "Check if email is already exists")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "500", description = "Generic error")
	})
	@GetMapping(path = "email-unique/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CheckResultDto> checkUniqueEmail(@PathVariable("email") String email)
			throws WorkstocksBusinessException;

}
