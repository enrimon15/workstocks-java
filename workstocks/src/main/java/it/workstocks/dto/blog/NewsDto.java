package it.workstocks.dto.blog;

import java.util.Base64;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import it.workstocks.dto.BaseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsDto extends BaseDto<String> {
	private int MAX_RECENT_COMMENT_SIZE = 5;
	
	private String id;
	
	@NotBlank
	private String title;
	
	@NotBlank
	private String body;
	
	private byte[] image;
	
	private Set<LikeDto> likes;
	
	@Size(max = 5)
	private Set<CommentDto> recentComments;
	
	private int commentSize;
	
	public String getBase64Image() {
		if (this.image == null) return null;
		return Base64.getEncoder().encodeToString(this.image);
	}
	
	public boolean isLikedByUser(Long userId) {
		if (likes == null || likes.isEmpty()) return false;
		return likes.stream().anyMatch(like -> like.getUserId().equals(userId));
	}
	
	public String getBodyTextPlain() {
		return body.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ").replaceAll("&nbsp;"," ");
	}
}
