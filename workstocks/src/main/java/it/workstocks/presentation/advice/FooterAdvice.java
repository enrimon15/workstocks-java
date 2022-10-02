package it.workstocks.presentation.advice;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import it.workstocks.dto.blog.NewsDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.service.NewsService;

@ControllerAdvice
public class FooterAdvice {

	@Autowired
	private NewsService newsService;

	/**
	 * Dati per popolare le news nel footer, viene usata una cache nel service per
	 * evitare una lettura a db per ogni richiesta
	 */
	@ModelAttribute(name = "footer")
	public Set<NewsDto> prepareFooter() throws WorkstocksBusinessException {
		return newsService.findLatests(5);
	}

}
