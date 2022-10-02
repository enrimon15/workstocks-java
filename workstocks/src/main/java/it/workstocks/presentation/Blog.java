package it.workstocks.presentation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import it.workstocks.dto.blog.CommentDto;
import it.workstocks.dto.blog.NewsDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.security.Roles;
import it.workstocks.service.NewsService;
import it.workstocks.utils.AuthUtility;

@Controller
@RequestMapping(path = Routes.ROOT_PUBLIC)
public class Blog {
	
	@Autowired
	private NewsService newsService;
	
	@Autowired
	private TemplateEngine templateEngine;

	@GetMapping(Routes.BLOG_NEWS)
	public String news(@RequestParam(required = false) String id, Model model) throws WorkstocksBusinessException {	

		if (id != null) {
			model.addAttribute("latestsNews", newsService.findLatests(4));
			model.addAttribute("comment", new CommentDto());
			model.addAttribute("news", newsService.findById(id, false));
			return Templates.BLOG_DETAILS.getTemplate();
		} else {
			return Templates.BLOG.getTemplate();
		}	
	}
	
	@PostMapping(Routes.BLOG_NEWS_DATA)
	@ResponseBody
	public ResponseEntity<PaginatedResponse> newsBlog(@RequestBody PaginatedRequest request) throws WorkstocksBusinessException {
		PaginatedDtoResponse<NewsDto> resp = newsService.findAllPaginatedTitleFiltering(
				request.getPageNumber(), 
				request.getFilters() != null ? request.getFilters().getNewsSearchQuery() : null
		);
		
		Context context = new Context();
		context.setLocale(LocaleContextHolder.getLocale());

		Map<String, Object> variables = new HashMap<>();
		variables.put("newsList", resp.getElements());
		context.setVariables(variables);

		PaginatedResponse result = resp.getResponse();
		result.setData(templateEngine.process(Templates.BLOG_DATA.getTemplate(), context));
		return ResponseEntity.ok(result);
	}
	
	@PostMapping(Routes.BLOG_NEWS_COMMENTS)
	@ResponseBody
	public ResponseEntity<PaginatedResponse> newsComments(@RequestParam String newsId, @RequestBody PaginatedRequest request) throws WorkstocksBusinessException {
		PaginatedDtoResponse<CommentDto> resp = newsService.findCommentsPaginatedByNewsId(newsId, request.getPageNumber());
		
		Context context = new Context();
		context.setLocale(LocaleContextHolder.getLocale());

		Map<String, Object> variables = new HashMap<>();
		variables.put("comments", resp.getElements());
		variables.put("currentUserID", AuthUtility.hasRole(Roles.APPLICANT) ? AuthUtility.getCurrentApplicant().getId() : null);
		context.setVariables(variables);

		PaginatedResponse result = resp.getResponse();
		result.setData(templateEngine.process(Templates.BLOG_NEWS_COMMENTS.getTemplate(), context));
		return ResponseEntity.ok(result);
	}

}
