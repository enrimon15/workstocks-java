package it.workstocks.presentation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.workstocks.dto.blog.NewsDto;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.service.NewsService;
import it.workstocks.utils.FileUtils;

@Controller
@RequestMapping(Routes.ROOT_ADMIN)
public class Admin {
	
	@Autowired
	private NewsService newsService;
	
	@GetMapping(Routes.NEWS_LIST)
	public String manageNewsList(@RequestParam(defaultValue = "1") Integer page, Model model) throws WorkstocksBusinessException {	
		PaginatedDtoResponse<NewsDto> newsList = newsService.findAllNewsPaginatedAdmin(page);
		model.addAttribute("newsList", newsList);
		return Templates.ADMIN_NEWS.getTemplate();
	}
	
	@GetMapping(Routes.NEWS)
	public String manageNews(@RequestParam(required=false) String id, Model model) throws WorkstocksBusinessException {	
		NewsDto newsDto = (id != null) ? newsService.findById(id, true) : new NewsDto();
		model.addAttribute("news", newsDto);
		return Templates.ADMIN_NEWS_DETAILS.getTemplate();
	}
	
	@PostMapping(Routes.NEWS)
	public String upsertNews(@Valid @ModelAttribute("news") NewsDto newsDto, Errors errors, Model model, RedirectAttributes redirectAttributes, HttpServletRequest req) throws WorkstocksBusinessException {	
		if (errors.hasErrors()) {
			return Templates.ADMIN_NEWS_DETAILS.getTemplate();
		}
		
		try {
			byte[] image;
			
			Part blogPhoto = req.getPart("blogPhoto");
			if ((blogPhoto == null || blogPhoto.getSize() <= 0) && newsDto.getId() == null) {
				errors.rejectValue("image", "blog.admin-create-imageError");
				return Templates.ADMIN_NEWS_DETAILS.getTemplate();
			}
			
			if (!FileUtils.checkFileSizeInMB(blogPhoto, 10L)) {
				errors.rejectValue("image", "blog.admin-create-imageError");
				return Templates.ADMIN_NEWS_DETAILS.getTemplate();
			}
			
			image = FileUtils.getBinaryData(blogPhoto);
			if (image != null) newsDto.setImage(image);
		} catch (IOException | ServletException e) {
			throw new WorkstocksBusinessException("Errore nell'acquisizione dell'immagine", e);
		}
		
		newsService.upsertNews(newsDto);
		
		if (newsDto.getId() != null) {
			redirectAttributes.addFlashAttribute("msgSuccess", "blog.admin-update-msgSuccess");
			return "redirect:"+Routes.ROOT_ADMIN+Routes.NEWS_LIST;
		} else {
			redirectAttributes.addFlashAttribute("msgSuccess", "blog.admin-create-msgSuccess");
			return "redirect:"+Routes.ROOT_ADMIN+Routes.NEWS_LIST;
		}
		
	}
	
	@GetMapping(Routes.NEWS_DELETE)
	public String editNews(@RequestParam String id, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {	
		newsService.deleteNewsById(id);
		redirectAttributes.addFlashAttribute("msgSuccess", "blog.admin-delete-msgSuccess");
		return "redirect:"+Routes.ROOT_ADMIN+Routes.NEWS_LIST;
	}

}
