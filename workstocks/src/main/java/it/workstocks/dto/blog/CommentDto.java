package it.workstocks.dto.blog;

import java.util.Base64;

import javax.validation.constraints.NotBlank;

import it.workstocks.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto extends BaseDto<String> {
	private String id;
	
	private Long userId;
	
	private String userName;
	
	private byte[] userAvatar;
	
	@NotBlank
	private String body;
	
	public String getBase64UserAvatar() {
		if (userAvatar == null) return null;
		return Base64.getEncoder().encodeToString(this.userAvatar);
	}
}
