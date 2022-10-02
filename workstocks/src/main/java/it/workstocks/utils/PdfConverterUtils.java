package it.workstocks.utils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.layout.font.FontProvider;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.dto.user.applicant.ApplicantDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.presentation.Templates;
import it.workstocks.service.ProfileService;

@Component
public class PdfConverterUtils {

	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private WorkstocksProperties props;
	
	public byte[] generateCVPdfFromHtmlTemplate() throws WorkstocksBusinessException {
		ApplicantDto dto = (ApplicantDto) profileService.loadFullUserById(AuthUtility.getCurrentApplicant().getId(), ApplicantDto.class);

		Context context = new Context();
		Map<String, Object> variables = new HashMap<>();
        variables.put("user", dto);
        variables.put("baseUrl", props.getSiteUrl());
        context.setVariables(variables);
        String templateHTML = templateEngine.process(Templates.CV_TEMPLATE.getTemplate(), context);
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
        
        ConverterProperties converterProperties = new ConverterProperties();
        FontProvider fontProvider  = new FontProvider();
        fontProvider.addDirectory("fonts");
        fontProvider.addStandardPdfFonts();
        fontProvider.addSystemFonts(); //for fallback
        converterProperties.setFontProvider(fontProvider);
        
        HtmlConverter.convertToPdf(templateHTML, buffer, converterProperties);
        byte[] pdfAsBytes = buffer.toByteArray();
        return pdfAsBytes;
    }
}
