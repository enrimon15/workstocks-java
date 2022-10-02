package it.workstocks.dto.blog;

import java.time.LocalDateTime;
import java.util.Base64;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {
	private LocalDateTime createdAt;
	
	private String id;
	
	private Long userId;
	
	private String userName;
	
	private String userAvatar;
	
	@NotBlank
	private String body;
	
	public void convertBase64UserAvatar(byte[] avatar) {
		if (avatar == null) this.userAvatar = null;
		this.userAvatar = Base64.getEncoder().encodeToString(avatar);
	}
}
