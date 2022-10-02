package it.workstocks.dto.user.applicant;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import it.workstocks.dto.AddressDto;
import it.workstocks.dto.user.UserDto;
import it.workstocks.dto.user.applicant.cv.CertificationDto;
import it.workstocks.dto.user.applicant.cv.ExperienceDto;
import it.workstocks.dto.user.applicant.cv.QualificationDto;
import it.workstocks.dto.user.applicant.cv.SkillDto;
import it.workstocks.entity.enums.Gender;
import it.workstocks.validator.url.UrlConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantDto extends UserDto {
	@Size(min = 2, max = 30)
	private String jobTitle;
	
	@Size(min = 2, max = 300)
	private String overview;
	
	@UrlConstraint
	private String website;
	
	@Size(min = 5, max = 11)
	private String phoneNumber;
	
	@Min(value = 0)
	private Integer minimumExpectedSalary;
	
	@Valid
	private AddressDto address;
	
	private byte[] curricula;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	@NotNull
	private Gender gender;
	
	@Valid
	private Set<SkillDto> skills;
	
	@Valid
	private Set<CertificationDto> certifications;
	
	@Valid
	private Set<QualificationDto> qualifications;
	
	@Valid
	private Set<ExperienceDto> experiences;
}
