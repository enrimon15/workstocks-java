package it.workstocks.presentation.applicant;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.workstocks.dto.blog.CommentDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.presentation.Routes;
import it.workstocks.presentation.Templates;
import it.workstocks.service.NewsService;

@Controller
@RequestMapping(path = Routes.ROOT_APPLICANT)
public class ApplicantBlog {

	@Autowired
	private NewsService newsService;

	@GetMapping(Routes.BLOG_LIKE)
	@ResponseBody
	public ResponseEntity<Map<String, String>> addOrRemoveLike(@RequestParam("id") String newsId, @RequestParam("opType") String opType)
			throws WorkstocksBusinessException {
		boolean isAdding = opType.equalsIgnoreCase("add") ? true : false;
		newsService.addOrRemoveLike(newsId, isAdding);

		Map<String, String> result = new HashMap<>();
		result.put("url", Routes.ROOT_APPLICANT + Routes.BLOG_LIKE + "?id=" + newsId + "&opType=" + (isAdding ? "remove" : "add"));
		result.put("opType", opType);

		return ResponseEntity.ok(result);
	}

	@GetMapping(Routes.BLOG_COMMENT_DELETE)
	public String deleteComment(@RequestParam("commentId") String commentId,
			RedirectAttributes redirectAttributes, @RequestHeader("referer") String referer)
			throws WorkstocksBusinessException {
		newsService.deleteComment(commentId);
		redirectAttributes.addFlashAttribute("msgSuccess", "blog.successDelete");
		return "redirect:" + referer;
	}

	@PostMapping(Routes.BLOG_COMMENT)
	public String upsertNews(@Valid @ModelAttribute("comment") CommentDto commentDto, Errors errors,
			@RequestParam("newsId") String newsId, RedirectAttributes redirectAttributes,
			@RequestHeader("referer") String referer) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			return Templates.BLOG_DETAILS.getTemplate();
		}

		newsService.addComment(commentDto, newsId);
		redirectAttributes.addFlashAttribute("msgSuccess", "blog.success");
		return "redirect:" + referer;
	}

}
