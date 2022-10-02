package it.workstocks.service;

import it.workstocks.dto.blog.CommentDto;
import it.workstocks.dto.blog.NewsDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.exception.WorkstocksBusinessException;


public interface NewsService {

	PaginatedDtoResponse<NewsDto> findAllPaginatedTitleFiltering(Integer pageNumber, Integer pageSize, String titleFilter);

	NewsDto findById(String id) throws WorkstocksBusinessException;
	
	byte[] findPhotoById(String id) throws WorkstocksBusinessException;
	
	boolean checkUserLike(String newsId) throws WorkstocksBusinessException;

	void addLike(String newsId) throws WorkstocksBusinessException;
	
	void removeLike(String newsId) throws WorkstocksBusinessException;

	void deleteComment(String commentId, String newsId) throws WorkstocksBusinessException;

	String addComment(CommentDto commentDto, String newsId) throws WorkstocksBusinessException;

	PaginatedDtoResponse<CommentDto> findCommentsPaginatedByNewsId(String newsId, Integer pageNumber, Integer limit) throws WorkstocksBusinessException;
	
	int countNewsComments(String newsId);
}
