package it.workstocks.rest.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.blog.CommentDto;
import it.workstocks.dto.blog.NewsDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.utility.CheckResultDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.rest.NewsV1;
import it.workstocks.service.NewsService;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;
import it.workstocks.validator.QueryParamValidator;

@RestController()
public class NewsV1Impl implements NewsV1 {
	
	@Autowired
	private NewsService newsService;
	
	@Autowired
	private WorkstocksProperties prop;
	
	@Autowired
	private QueryParamValidator queryParamValidator;
	
	@Autowired
	private Translator translator; 
	
	private UriComponentsBuilder uriBuilder;
	
	@PostConstruct
	private void prepareBaseUri() {
		uriBuilder = UriComponentsBuilder.newInstance().path(prop.getSite().getUrl() + "v1/news");
	}

	@Override
	public ResponseEntity<PaginatedDtoResponse<NewsDto>> getNewsList(String title, Integer page, Integer limit) throws WorkstocksBusinessException {
		queryParamValidator.validateInteger("limit", limit, 1, 10);
		PaginatedDtoResponse<NewsDto> newsList = newsService.findAllPaginatedTitleFiltering(page, limit, title);
		for (NewsDto news : newsList.getElements()) {
			news.setDetailsURL(uriBuilder.cloneBuilder().path("/{newsId}").buildAndExpand(news.getId()).toString());
			news.setCommentSize(newsService.countNewsComments(news.getId()));
			news.setComments(uriBuilder.cloneBuilder().path("/{newsId}/comments").buildAndExpand(news.getId()).toString());
			String bodyTextPlain = news.getBody().replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ").replaceAll("&nbsp;"," ");
			news.setBody(
					bodyTextPlain.length() > 100 ? (bodyTextPlain.substring(0, 100) + "...") : bodyTextPlain
			);
		}
			
		return ResponseEntity.ok(newsList);
	}

	@Override
	public ResponseEntity<NewsDto> getNewsById(String newsId) throws WorkstocksBusinessException {
		NewsDto news = newsService.findById(newsId);
		news.setComments(uriBuilder.cloneBuilder().path("/{newsId}/comments").buildAndExpand(news.getId()).toString());
		return ResponseEntity.ok(news);
	}
	
	@Override
	public ResponseEntity<byte[]> getNewsPhotoById(String newsId) throws WorkstocksBusinessException {
		byte[] photo = newsService.findPhotoById(newsId);
		if (photo == null || photo.length <= 0) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.PHOTO_NOT_FOUND, new String[] {"news",newsId}), HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(photo);
	}

	@SuppressWarnings("static-access")
	@Override
	public ResponseEntity<PaginatedDtoResponse<CommentDto>> getNewsComments(String newsId, Integer page, Integer limit) throws WorkstocksBusinessException {
		queryParamValidator.validateInteger("limit", limit, 1, 10);
		PaginatedDtoResponse<CommentDto> comments = newsService.findCommentsPaginatedByNewsId(newsId, page, limit);
		return ResponseEntity.ok(comments);
	}

	@Override
	public ResponseEntity<Void> deleteNewsComments(String newsId, String commentId) throws WorkstocksBusinessException {
		newsService.deleteComment(commentId, newsId);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> addNewsComment(String newsId, CommentDto comment, Errors errors) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			throw new WorkstocksBusinessException(
					translator.toLocale(ErrorUtils.WRONG_PAYLOAD, new String[] {"comment"}), ErrorUtils.getErrorList(errors), HttpStatus.BAD_REQUEST);
		}
		String commentId = newsService.addComment(comment, newsId);
		return ResponseEntity.created(uriBuilder.cloneBuilder().path("/{newsId}/comments/{commentsId}").buildAndExpand(newsId, commentId).toUri()).build();
	}

	@Override
	public ResponseEntity<Void> addNewsLike(String newsId) throws WorkstocksBusinessException {
		newsService.addLike(newsId);
		return ResponseEntity.created(uriBuilder.cloneBuilder().path("/{newsId}/likes").buildAndExpand(newsId).toUri()).build();
	}

	@Override
	public ResponseEntity<Void> removeNewsLike(String newsId) throws WorkstocksBusinessException {
		newsService.removeLike(newsId);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<CheckResultDto> checkNewsLike(String newsId) throws WorkstocksBusinessException {
		return ResponseEntity.ok(new CheckResultDto(newsService.checkUserLike(newsId)));
	}

}
