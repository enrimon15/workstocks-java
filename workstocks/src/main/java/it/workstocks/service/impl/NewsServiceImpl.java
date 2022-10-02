package it.workstocks.service.impl;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dto.blog.CommentDto;
import it.workstocks.dto.blog.NewsDto;
import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.entity.blog.Comment;
import it.workstocks.entity.blog.Like;
import it.workstocks.entity.blog.News;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.blog.CommentRepository;
import it.workstocks.repository.blog.NewsRepository;
import it.workstocks.repository.user.ApplicantRepository;
import it.workstocks.service.NewsService;
import it.workstocks.utils.AuthUtility;

@Service
public class NewsServiceImpl implements NewsService{
	
	@Autowired
	private NewsRepository newsRepository;
	
	@Autowired
	private ApplicantRepository applicantRepository;
	
	@Autowired
	EntityMapper mapper;
	
	@Autowired
	CommentRepository commentRepository;
	
	private News findOptionalNews(String id) throws WorkstocksBusinessException {
		Optional<News> news = newsRepository.findById(id);
		if (news.isPresent()) {
			return news.get();
		} else {
			throw new WorkstocksBusinessException("News non trovata");
		}
	}
	
	private Sort getSortByCreationDate() {
		return Sort.by(Sort.Direction.DESC, "createdAt");
	}
	
	private PaginatedDtoResponse<NewsDto> getPaginatedNewsDto(Page<News> newsPaginated) {
		Set<NewsDto> content = mapper.toDtoNewsCollection(new LinkedHashSet<>(newsPaginated.getContent()));
		
		PaginatedResponse paginatedReponse = new PaginatedResponse();
		paginatedReponse.setTotalElements(newsPaginated.getTotalElements());
		paginatedReponse.setPageNumber(newsPaginated.getPageable().getPageNumber() + 1);
		paginatedReponse.setTotalPages(newsPaginated.getTotalPages());
		PaginatedDtoResponse<NewsDto> response = new PaginatedDtoResponse<>();
		response.setResponse(paginatedReponse); 
		response.setElements(content);
		
		return response;
	}
	
	@Override
	public PaginatedDtoResponse<NewsDto> findAllPaginatedTitleFiltering(int pageNumber, String titleFilter) throws WorkstocksBusinessException {
		Pageable pageable = PageRequest.of(pageNumber - 1, 9, getSortByCreationDate());
		
		Page<News> newsPaginated = null;
		if (titleFilter == null || titleFilter.trim().isEmpty()) {
			newsPaginated = newsRepository.findAll(pageable);
		} else {
			newsPaginated = newsRepository.findByTitleContainingIgnoreCase(titleFilter, pageable);
		}
		
		return getPaginatedNewsDto(newsPaginated);
	}
	
	@Override
	public PaginatedDtoResponse<NewsDto> findAllNewsPaginatedAdmin(int pageNumber) throws WorkstocksBusinessException {
		Pageable pageable = PageRequest.of(pageNumber - 1, 10, getSortByCreationDate());
		Page<News> newsPaginated = newsRepository.findAll(pageable);
		return getPaginatedNewsDto(newsPaginated);
	}
	
	private void extendComments(Set<CommentDto> comments) throws WorkstocksBusinessException {
		if (comments != null) {
			for (CommentDto comment : comments) {
				Optional<Applicant> user = applicantRepository.findById(comment.getUserId());
				if (user.isEmpty()) throw new WorkstocksBusinessException("Applicant comment not found");
				comment.setUserName(user.get().getName());
				comment.setUserAvatar(user.get().getAvatar());
			}
		}
	}

	@Override
	public NewsDto findById(String id, boolean isAdminDetail) throws WorkstocksBusinessException {
		NewsDto news = mapper.toDto(findOptionalNews(id));
		// se admin che richiede la news per la modifica non prendo i commenti
		if (!isAdminDetail) {
			news.setCommentSize(commentRepository.countByNewsId(news.getId()));
			extendComments(news.getRecentComments());
		}
		
		return news;
	}
	
	@Override
	public PaginatedDtoResponse<CommentDto> findCommentsPaginatedByNewsId(String newsId, int pageNumber) throws WorkstocksBusinessException {
		PaginatedResponse paginatedReponse = new PaginatedResponse();
		paginatedReponse.setPageNumber(pageNumber);
		
		Pageable pageable = PageRequest.of(pageNumber - 1, News.MAX_RECENT_COMMENT_SIZE, getSortByCreationDate());
		Page<Comment> commentsPaginated = commentRepository.findByNewsId(newsId, pageable);
		Set<CommentDto> content = mapper.toDto(new LinkedHashSet<>(commentsPaginated.getContent()));
		extendComments(content);
		paginatedReponse.setTotalElements(commentsPaginated.getTotalElements());
		paginatedReponse.setTotalPages(commentsPaginated.getTotalPages());
		
		PaginatedDtoResponse<CommentDto> response = new PaginatedDtoResponse<>();
		response.setResponse(paginatedReponse); 
		response.setElements(content);
		
		return response;
	}
	
	// admin

	@Override
	@CacheEvict(value = "footerNews", allEntries = true)
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void upsertNews(NewsDto newsDto) throws WorkstocksBusinessException {
		News news = null;
		if (newsDto.getId() != null) {
			news = findOptionalNews(newsDto.getId());
			mapper.updateNewsEntity(newsDto, news);
		} else {
			news = mapper.toEntity(newsDto);
		}		
		
		newsRepository.save(news);
	}

	@Override
	@CacheEvict(value = "footerNews", allEntries = true)
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void deleteNewsById(String id) throws WorkstocksBusinessException {
		newsRepository.deleteById(id);
		commentRepository.deleteAllByNewsId(id);
	}
	
	// applicant

	@Override
	@CacheEvict(value = "footerNews", allEntries = true)
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addOrRemoveLike(String newsId, boolean isAdding) throws WorkstocksBusinessException {
		News news = findOptionalNews(newsId);
		Long userId = AuthUtility.getCurrentApplicant().getId();
		
		if (isAdding) {
			Like like = new Like();
			like.setUserId(userId);
			news.getLikes().add(like);
		} else {
			news.getLikes().removeIf(like -> like.getUserId().equals(userId));
		}
		
		newsRepository.save(news);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void deleteComment(String commentId) throws WorkstocksBusinessException {
		Optional<Comment> comment = commentRepository.findById(commentId);
		if (comment.isPresent()) {
			if (!AuthUtility.compareCurrentUser(comment.get().getUserId())) {
				throw new AccessDeniedException("User not authorized to delete comment");
			}
			commentRepository.delete(comment.get());
			
			News news = comment.get().getNews();
			boolean isRemoved = news.removeComment(commentId);
			if (isRemoved) {
				Comment newerComment = commentRepository.findFirstByNewsIdOrderByCreatedAt(news.getId());
				if (newerComment != null && news.getRecentComments().stream().noneMatch(comm -> comm.getId() != newerComment.getId())) {
					news.addComment(newerComment);
				}
				newsRepository.save(news);
			}
			
		} else {
			throw new WorkstocksBusinessException("Comment not found");
		}
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addComment(CommentDto commentDto, String newsId) throws WorkstocksBusinessException {
		News news = findOptionalNews(newsId);
		
		Comment comment = mapper.toEntity(commentDto);
		comment.setUserId(AuthUtility.getCurrentApplicant().getId());
		comment.setNews(news);
		commentRepository.save(comment);
		
		news.addComment(comment);
		newsRepository.save(news);
	}

	@Override
	@Cacheable(value = "footerNews")
	public Set<NewsDto> findLatests(int numberOfNews) throws WorkstocksBusinessException {
		Pageable pageable = PageRequest.of(0, numberOfNews, getSortByCreationDate());
		Page<News> latestNews = newsRepository.findAll(pageable);
		return mapper.toDtoNewsCollection(new LinkedHashSet<>(latestNews.getContent()));
	}

}
