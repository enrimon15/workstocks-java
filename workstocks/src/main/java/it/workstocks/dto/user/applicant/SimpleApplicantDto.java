package it.workstocks.dto.user.applicant;

import java.time.LocalDate;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.sun.istack.NotNull;

import it.workstocks.dto.AddressDto;
import it.workstocks.dto.user.UserDto;
import it.workstocks.entity.enums.Gender;
import it.workstocks.validator.url.UrlConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleApplicantDto extends UserDto {
	
	@Size(min = 1, max = 100)
	private String jobTitle;
	
	@Size(min = 1, max = 5000)
	private String overview;
	
	@Size(min = 5, max = 11)
	private String phoneNumber;
	
	@UrlConstraint
	private String website;
	
	@Min(value = 1L)
	@Max(value = 9999L)
	private Integer minimumExpectedSalary;
	
	@Valid
	private AddressDto address;
	
	private byte[] curricula;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	@NotNull
	private Gender gender;
}
