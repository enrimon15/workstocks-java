package it.workstocks.entity.blog;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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
public class News extends BaseMongoEntity<String> {
	
	@Id
	private String id;
	
	private String title;
	
	private String body;
	
	private byte[] image;
	
	// embedded
	private Set<Like> likes = new LinkedHashSet<>();
	
	// subset pattern (embedded) con i commenti recenti
	private Set<Comment> recentComments = new LinkedHashSet<>();
	
	@Transient
	public static final int MAX_RECENT_COMMENT_SIZE = 5;
	
	// aggiunge il commento tra i commenti recenti
	// se la max size dei commenti recenti è stata raggiunta viene inserito al posto del più vecchio
	@Transient
	public void addComment(Comment  newComment) {
		if (this.recentComments.size() >= MAX_RECENT_COMMENT_SIZE) {	
			Comment olderComment = this.recentComments.stream().min(comparatorCommentByDate()).get();
			if (olderComment != null) this.recentComments.remove(olderComment);
		}
		
		this.recentComments.add(newComment);
	}
	
	// se il commento con l'id passato in input è presente nei commenti recenti lo rimuove
	// true se trovato e rimosso, false altrimenti
	@Transient
	public boolean removeComment(String commentId) {
		boolean isRemoved = false;
		if (!this.recentComments.isEmpty()) {
			isRemoved = this.recentComments.removeIf( comm -> (commentId.equals(comm.getId())) );
		}
		
		return isRemoved;
	}
	
	// comparator by createAt date
	@Transient
	private Comparator<Comment> comparatorCommentByDate() {
		Comparator<Comment> comparator = Comparator.comparing( Comment::getCreatedAt );
		return comparator;
	}

}
