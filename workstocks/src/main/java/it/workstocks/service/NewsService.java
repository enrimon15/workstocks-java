package it.workstocks.service;

import java.util.Set;

import it.workstocks.dto.blog.CommentDto;
import it.workstocks.dto.blog.NewsDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.exception.WorkstocksBusinessException;


public interface NewsService {
	
	PaginatedDtoResponse<NewsDto> findAllNewsPaginatedAdmin(int pageNumber) throws WorkstocksBusinessException;

	PaginatedDtoResponse<NewsDto> findAllPaginatedTitleFiltering(int pageNumber, String titleFilter) throws WorkstocksBusinessException;

	NewsDto findById(String id, boolean isAdminDetail) throws WorkstocksBusinessException;
	
	void upsertNews(NewsDto newsDto) throws WorkstocksBusinessException;

	void deleteNewsById(String id) throws WorkstocksBusinessException;

	void addOrRemoveLike(String newsId, boolean isAdding) throws WorkstocksBusinessException;

	void deleteComment(String commentId) throws WorkstocksBusinessException;

	void addComment(CommentDto commentDto, String newsId) throws WorkstocksBusinessException;

	Set<NewsDto> findLatests(int numberOfNews) throws WorkstocksBusinessException;

	PaginatedDtoResponse<CommentDto> findCommentsPaginatedByNewsId(String newsId, int pageNumber) throws WorkstocksBusinessException;
}
