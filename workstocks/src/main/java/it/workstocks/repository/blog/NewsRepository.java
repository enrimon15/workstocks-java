package it.workstocks.repository.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.blog.News;

@Repository
public interface NewsRepository extends MongoRepository<News, String> {
	Page<News> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
