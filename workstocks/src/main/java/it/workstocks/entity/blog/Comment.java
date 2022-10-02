package it.workstocks.entity.blog;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import it.workstocks.entity.BaseMongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Comment extends BaseMongoEntity<String> {
	@Id
	private String id;
	
	private Long userId;
	
	private String body;
	
	// subset pattern (references) con la news
	@DBRef(lazy = true)
	News news;
}
