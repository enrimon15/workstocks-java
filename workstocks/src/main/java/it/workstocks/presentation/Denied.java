package it.workstocks.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class Denied {
	@GetMapping(Routes.DENIED)
	public String getDeniedPage(@RequestHeader(value = "referer", required = false) final String referer, Model model) {
		model.addAttribute("referer", referer);
		return Templates.DENIED.getTemplate();
	}
}
