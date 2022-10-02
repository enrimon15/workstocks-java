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
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.ApplicantRepository;

@Component
public class PdfConverterUtils {

	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private ApplicantRepository applicantRepository;
	
	@Autowired
	WorkstocksProperties props;
	
	private static final String CV_TEMPLATE = "cv-pdf";
	
	public byte[] generateCVPdfFromHtmlTemplate() throws WorkstocksBusinessException {
		Applicant applicant = applicantRepository.findById(AuthUtility.getCurrentApplicant().getId()).get();

		Context context = new Context();
		Map<String, Object> variables = new HashMap<>();
        variables.put("user", applicant);
        variables.put("baseUrl", props.getSite().getUrl());
        context.setVariables(variables);
        String templateHTML = templateEngine.process(CV_TEMPLATE, context);
        
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
