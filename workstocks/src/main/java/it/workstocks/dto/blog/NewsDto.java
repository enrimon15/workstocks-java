package it.workstocks.dto.blog;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsDto {
	
	private LocalDateTime createdAt;
	
	private String id;
	
	@NotBlank
	private String title;
	
	@NotBlank
	private String body;
	
	private String photo;
	
	private int likesNumber;
	
	private int commentSize;
	
	private String comments;
	
	private String detailsURL;
}
