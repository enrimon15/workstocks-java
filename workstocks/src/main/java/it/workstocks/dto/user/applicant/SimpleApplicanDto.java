package it.workstocks.dto.user.applicant;

import it.workstocks.dto.AddressDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleApplicanDto {
	
	private Long id;
	
	private String name;
	
	private String surname;
	
	private String photo;
	
	private AddressDto address;
	
	private String jobTitle;
	
	private String detailsURL;

}
