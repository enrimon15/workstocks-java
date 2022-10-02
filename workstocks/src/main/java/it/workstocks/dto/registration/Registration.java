package it.workstocks.dto.registration;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import it.workstocks.entity.enums.WorkingPlaceType;
import it.workstocks.validator.email.EmailUniqueConstraint;
import it.workstocks.validator.url.UrlConstraint;
import it.workstocks.validator.vatnumber.VatNumberConstraint;
import it.workstocks.validator.vatnumber.VatNumberUniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Registration {
	@NotNull
	private UserType userType;

	@Size(min = 3, max = 60)
	@NotBlank
	private String name;

	@Size(min = 3, max = 60)
	@NotBlank
	private String surname;

	@Email
	@NotBlank
	@EmailUniqueConstraint
	private String email;

	@Size(min = 7, max = 11)
	private String contactPhone;
	
	@NotBlank
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$", message = "{loginAndRegister.passwordRegex}")
	private String password;

	private String passwordConfirmation;

	@Size(min = 3, max = 60)
	private String companyName;

	@VatNumberConstraint
	@VatNumberUniqueConstraint
	private Long vatNumber;

	@Size(min = 2, max = 5)
	private String companyForm;

	@Min(value = 1600)
	private Integer foundationYear;

	@Min(value = 1)
	private Integer employeesNumber;

	@UrlConstraint
	private String website;

	@Size(min = 2, max = 50)
	private String address;

	@Size(min = 2, max = 60)
	private String city;

	@Size(min = 2, max = 60)
	private String country;

	private WorkingPlaceType workingPlaceType;
}
