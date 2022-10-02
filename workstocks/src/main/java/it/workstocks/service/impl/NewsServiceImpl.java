package it.workstocks.service.impl;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
import it.workstocks.repository.ApplicantRepository;
import it.workstocks.repository.CommentRepository;
import it.workstocks.repository.NewsRepository;
import it.workstocks.service.NewsService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;

@Service
public class NewsServiceImpl implements NewsService{
	
	@Autowired
	private NewsRepository newsRepository;
	
	@Autowired
	private EntityMapper mapper;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private ApplicantRepository applicantRepository;
	
	@Autowired
	private Translator translator;
	
	private News findOptionalNews(String id) throws WorkstocksBusinessException {
		Optional<News> news = newsRepository.findById(id);
		if (news.isPresent()) {
			return news.get();
		} else {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.NOT_FOUND, new Object[] {"news", id}),HttpStatus.NOT_FOUND);
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
		paginatedReponse.setPageSize(newsPaginated.getNumberOfElements());
		PaginatedDtoResponse<NewsDto> response = new PaginatedDtoResponse<>();
		response.setResponse(paginatedReponse); 
		response.setElements(content);
		
		return response;
	}
	
	@Override
	public PaginatedDtoResponse<NewsDto> findAllPaginatedTitleFiltering(Integer pageNumber, Integer pageSize, String titleFilter) {
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, getSortByCreationDate());
		
		Page<News> newsPaginated = null;
		if (titleFilter == null || titleFilter.trim().isEmpty()) {
			newsPaginated = newsRepository.findAll(pageable);
		} else {
			newsPaginated = newsRepository.findByTitleContainingIgnoreCase(titleFilter, pageable);
		}
		
		return getPaginatedNewsDto(newsPaginated);
	}
	
	@Override
	public int countNewsComments(String newsId) {
		return commentRepository.countByNewsId(newsId);
	}
	
	private void extendComments(Set<CommentDto> comments) throws WorkstocksBusinessException {
		if (comments != null) {
			for (CommentDto comment : comments) {
				Optional<Applicant> user = applicantRepository.findById(comment.getUserId());
				if (user.isEmpty()) throw new WorkstocksBusinessException("Applicant for comment not found", HttpStatus.NOT_FOUND);
				comment.setUserName(user.get().getName());
				comment.convertBase64UserAvatar(user.get().getAvatar());
			}
		}
	}
	
	@Override
	public byte[] findPhotoById(String id) throws WorkstocksBusinessException {
		News news = findOptionalNews(id);
		return news.getImage();
	}

	@Override
	public NewsDto findById(String id) throws WorkstocksBusinessException {
		NewsDto news = mapper.toDto(findOptionalNews(id));
		news.setCommentSize(commentRepository.countByNewsId(news.getId()));
		return news;
	}
	
	@Override
	public PaginatedDtoResponse<CommentDto> findCommentsPaginatedByNewsId(String newsId, Integer pageNumber, Integer limit) throws WorkstocksBusinessException {
		findOptionalNews(newsId);
		
		PaginatedResponse paginatedReponse = new PaginatedResponse();
		paginatedReponse.setPageNumber(pageNumber);
		
		Pageable pageable = PageRequest.of(pageNumber - 1, limit, getSortByCreationDate());
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
	
	// applicant
	
	@Override
	public boolean checkUserLike(String newsId) throws WorkstocksBusinessException {
		News news = findOptionalNews(newsId);
		Long userId = AuthUtility.getCurrentApplicant().getId();
		return news.getLikes().stream().anyMatch(like -> like.getUserId().equals(userId));
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addLike(String newsId) throws WorkstocksBusinessException {
		News news = findOptionalNews(newsId);
		Long userId = AuthUtility.getCurrentApplicant().getId();
		
		if (news.getLikes().stream().anyMatch(like -> like.getUserId().equals(userId))) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.LIKE_ALREADY_SUBMIT, new Object[] {newsId, userId}),HttpStatus.CONFLICT);
		}
		
		Like like = new Like();
		like.setUserId(userId);
		news.getLikes().add(like);		
		newsRepository.save(news);
	}
	
	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void removeLike(String newsId) throws WorkstocksBusinessException {
		News news = findOptionalNews(newsId);
		Long userId = AuthUtility.getCurrentApplicant().getId();
		boolean isPresent = news.getLikes().removeIf(like -> like.getUserId().equals(userId));
		
		if (!isPresent) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.LIKE_NOT_FOUND, new Object[] {newsId, userId}),HttpStatus.NOT_FOUND);
		}
		
		newsRepository.save(news);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void deleteComment(String commentId, String newsId) throws WorkstocksBusinessException {
		findOptionalNews(newsId);
		
		Optional<Comment> comment = commentRepository.findById(commentId);
		if (comment.isPresent() && comment.get().getNews().getId().equals(newsId)) {
			if (!AuthUtility.compareCurrentUser(comment.get().getUserId())) {
				throw new AccessDeniedException(String.format("User with id %d not authorized to delete comment with id %s", comment.get().getUserId(), commentId));
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
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.COMMENT_NOT_FOUND, new Object[] {commentId, newsId}),HttpStatus.NOT_FOUND);
		}
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public String addComment(CommentDto commentDto, String newsId) throws WorkstocksBusinessException {
		News news = findOptionalNews(newsId);
		
		Comment comment = new Comment();
		comment.setBody(commentDto.getBody());
		comment.setUserId(AuthUtility.getCurrentApplicant().getId());
		comment.setNews(news);
		comment = commentRepository.save(comment);
		
		news.addComment(comment);
		newsRepository.save(news);
		
		return comment.getId();
	}

}
