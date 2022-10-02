package it.workstocks.repository.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import it.workstocks.entity.blog.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
	Comment findFirstByNewsIdOrderByCreatedAt(String newsId);
	Page<Comment> findByNewsId(String newsId, Pageable pageableSorted);
	int countByNewsId(String newsId);
	void deleteAllByNewsId(String id);
}
