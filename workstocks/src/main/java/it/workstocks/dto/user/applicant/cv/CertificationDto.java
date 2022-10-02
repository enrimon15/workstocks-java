package it.workstocks.dto.user.applicant.cv;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.workstocks.validator.url.UrlConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificationDto {
	private Long id;
	
	@NotBlank
	@Size(min = 3, max = 40)
	private String name;
	
	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate date;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	
	@UrlConstraint
	private String url;
	
	@NotBlank
	@Size(min = 3, max = 100)
	private String credential;
	
	@NotBlank
	@Size(min = 3, max = 60)
	private String society;
	
	private boolean noExpiration;
}
