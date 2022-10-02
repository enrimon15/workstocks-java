package it.workstocks.rest;

import static it.workstocks.utils.AuthUtility.IS_APPLICANT;
import static it.workstocks.utils.AuthUtility.PERMIT_ALL;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.workstocks.dto.blog.CommentDto;
import it.workstocks.dto.blog.NewsDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.utility.CheckResultDto;
import it.workstocks.exception.WorkstocksBusinessException;

@RequestMapping(path = "/v1/news")
public interface NewsV1 {
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "NEWS")
	@Operation(summary = "Get list of filtered and paginated news (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameter", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PaginatedDtoResponse<NewsDto>> getNewsList(@RequestParam(required = false) String title, @RequestParam Integer page, @RequestParam Integer limit) throws WorkstocksBusinessException;
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "NEWS")
	@Operation(summary = "Get news by id (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "News not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@GetMapping(path = "{newsId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<NewsDto> getNewsById(@PathVariable("newsId") String newsId) throws WorkstocksBusinessException;
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "NEWS")
	@Operation(summary = "Get news photo by id (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "News or photo not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@GetMapping(path = "{newsId}/photo", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
	ResponseEntity<StreamingResponseBody> getNewsPhotoById(@PathVariable("newsId") String newsId) throws WorkstocksBusinessException;
	
	@PreAuthorize(PERMIT_ALL)
	@Tag(name = "NEWS")
	@Operation(summary = "Get list of paginated comments for news (NO AUTH)")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "400", description = "Invalid request parameter", content = @Content),
		@ApiResponse(responseCode = "404", description = "News or applicant for comment not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@GetMapping(path = "/{newsId}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<PaginatedDtoResponse<CommentDto>> getNewsComments(@PathVariable("newsId") String newsId, @RequestParam Integer page, @RequestParam Integer limit) throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "NEWS")
	@Operation(summary = "Remove user comment for news")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success", content = @Content),
		@ApiResponse(responseCode = "404", description = "News or comment not found", content = @Content),
		@ApiResponse(responseCode = "403", description = "Access denied to remove comment", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@DeleteMapping("/{newsId}/comments/{commentId}") 
	ResponseEntity<Void> deleteNewsComments(@PathVariable("newsId") String newsId, @PathVariable("commentId") String commentId) throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "NEWS")
	@Operation(summary = "Add user comment for news")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Operation success", content = @Content),
		@ApiResponse(responseCode = "404", description = "News not found", content = @Content),
		@ApiResponse(responseCode = "400", description = "Wrong comment payload", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@PostMapping(path = "/{newsId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Void> addNewsComment(@PathVariable("newsId") String newsId, @Valid @RequestBody CommentDto comment, Errors errors) throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "NEWS")
	@Operation(summary = "Add user like for news")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Operation success", content = @Content),
		@ApiResponse(responseCode = "404", description = "News not found", content = @Content),
		@ApiResponse(responseCode = "409", description = "Like for news is already present", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@PostMapping("/{newsId}/likes") 
	ResponseEntity<Void> addNewsLike(@PathVariable("newsId") String newsId) throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "NEWS")
	@Operation(summary = "Remove user like for news")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "Operation success", content = @Content),
		@ApiResponse(responseCode = "404", description = "News or like not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@DeleteMapping("/{newsId}/likes")
	ResponseEntity<Void> removeNewsLike(@PathVariable("newsId") String newsId) throws WorkstocksBusinessException;
	
	@PreAuthorize(IS_APPLICANT)
	@Tag(name = "NEWS")
	@Operation(summary = "Check if news is liked by logged user")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Operation success"),
		@ApiResponse(responseCode = "404", description = "News not found", content = @Content),
		@ApiResponse(responseCode = "500", description = "Generic error", content = @Content)
	})
	@GetMapping(path = "/{newsId}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CheckResultDto> checkNewsLike(@PathVariable("newsId") String newsId) throws WorkstocksBusinessException;
	
	
	
	

}
