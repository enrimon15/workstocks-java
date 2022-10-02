package it.workstocks.dto.user.applicant;

import java.time.LocalDate;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.workstocks.dto.AddressDto;
import it.workstocks.entity.enums.Gender;
import it.workstocks.validator.email.EmailUniqueConstraint;
import it.workstocks.validator.url.UrlConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BasicApplicant {
	
	private Optional<@NotBlank String> name;

	private Optional<@NotBlank String> surname;

	protected Optional<@Email @EmailUniqueConstraint @NotBlank String> email;

	private Optional<@Size(min = 2, max = 30) @NotBlank String> jobTitle;

	private Optional<@Size(min = 2, max = 10000) @NotBlank String> overview;

	private Optional<@UrlConstraint String> website;

	private Optional<@Size(min = 5, max = 11) String> phoneNumber;

	private Optional<@Min(value = 0) Integer> minimumExpectedSalary;

	private Optional<@Valid AddressDto> address;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Optional<LocalDate> dateOfBirth;

	private Optional<Gender> gender;

	private String skills;

	private String certifications;

	private String qualifications;

	private String experiences;
	
	private String photo;
	
	private boolean isCv;
}
