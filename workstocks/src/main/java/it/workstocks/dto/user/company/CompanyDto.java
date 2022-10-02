package it.workstocks.dto.user.company;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import it.workstocks.dto.AddressDto;
import it.workstocks.entity.pojo.ReviewAverageAndCountPojo;
import it.workstocks.validator.url.UrlConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDto {
	private Long id;
	
	@Size(min = 3, max = 60)
	@NotBlank
	private String name;
	
	@NotNull
	private Long vatNumber;
	
	@Size(min = 2, max = 5)
	@NotBlank
	private String companyForm;
	
	@Size(min = 3, max = 300)
	private String overview;
	
	@Min(value = 1L)
	@Max(value = 99999L)
	@NotNull
	private Integer employeesNumber;
	
	@Min(value = 1700)
	@NotNull
	private Integer foundationYear;
	
	@NotBlank
	@UrlConstraint
	private String website;
	
	private String email;
	
	@Size(min = 3, max = 80)
	private String slogan;
	
	@NotBlank
	@Size(min = 7, max = 11)
	private String telephone;
	
	private String workingPlaces;
	
	private String jobOffers;
	
	@Valid
	private AddressDto address;
	
	private String photo;
	
	@Valid
	private ReviewAverageAndCountPojo ratingStats;
}
