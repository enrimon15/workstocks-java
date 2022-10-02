package it.workstocks.presentation.applicant;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
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

import it.workstocks.dto.user.applicant.cv.CertificationDto;
import it.workstocks.dto.user.applicant.cv.ExperienceDto;
import it.workstocks.dto.user.applicant.cv.QualificationDto;
import it.workstocks.dto.user.applicant.cv.SkillDto;
import it.workstocks.entity.user.applicant.curricula.Skill;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.presentation.Routes;
import it.workstocks.presentation.Templates;
import it.workstocks.service.ApplicantService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.CvOperatonType;
import it.workstocks.utils.FileUtils;
import it.workstocks.utils.PdfConverterUtils;

@Controller
@RequestMapping(path = Routes.ROOT_ONLINE_CV)
public class ApplicantProfileCV {
	
	@Autowired
	private ApplicantService applicantService;
	
	@Autowired
	private PdfConverterUtils pdfConverterUtils;
	
	@GetMapping
	public String getOnlineCv(Model model) throws WorkstocksBusinessException {
		Long userId = AuthUtility.getCurrentApplicant().getId();
		
		model.addAttribute("qualifications", applicantService.findApplicantQualifications(userId));
		model.addAttribute("experiences", applicantService.findApplicantExperiences(userId));
		model.addAttribute("certificates", applicantService.findApplicantCertificates(userId));
		model.addAttribute("skills", applicantService.findApplicantSkills(userId));		
		
		model.addAttribute("qualification", new QualificationDto());
		model.addAttribute("experience", new ExperienceDto());
		model.addAttribute("certificate", new CertificationDto());
		model.addAttribute("skill", new Skill());
		
		return Templates.APPLICANT_PROFILE.getTemplate();
	}
	
	
	
	@PostMapping(Routes.QUALIFICATION)
	public String addApplicantQualification(@Valid @ModelAttribute("qualification") QualificationDto qualificationDto, Errors errors, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			return handlePopulateOnlineCvValidErrors(redirectAttributes);
		}
		
		applicantService.addApplicantQualification(qualificationDto);
		return successOnlineCv(redirectAttributes, "ocv.successQualification");
	}
	
	@PostMapping(Routes.EXPERIENCE)
	public String addApplicantExperience(@Valid @ModelAttribute("experience") ExperienceDto experienceDto, Errors errors, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			return handlePopulateOnlineCvValidErrors(redirectAttributes);
		}
		
		applicantService.addApplicantExperience(experienceDto);
		return successOnlineCv(redirectAttributes, "ocv.successExperience");
	}
	
	@PostMapping(Routes.CERTIFICATE)
	public String addApplicantCertificate(@Valid @ModelAttribute("certificate") CertificationDto certificationDto, Errors errors, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			return handlePopulateOnlineCvValidErrors(redirectAttributes);
		}
		
		applicantService.addApplicantCertificate(certificationDto);
		return successOnlineCv(redirectAttributes, "ocv.successCertificate");
	}
	
	@PostMapping(Routes.SKILL)
	public String addApplicantSkill(@Valid @ModelAttribute("skill") SkillDto skillDto, Errors errors, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			return handlePopulateOnlineCvValidErrors(redirectAttributes);
		}
		
		applicantService.addApplicantSkill(skillDto);
		return successOnlineCv(redirectAttributes, "ocv.successSkill");
	}
	
	@PostMapping(Routes.CV)
	public String addApplicantCV(@RequestParam(required = false) boolean autoGenerate, HttpServletRequest req, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException, IOException, ServletException {		
		byte[] cv = autoGenerate ? pdfConverterUtils.generateCVPdfFromHtmlTemplate() : FileUtils.getBinaryData(req.getPart("cv"));
		if (cv == null || cv.length <= 0) {
			return handlePopulateOnlineCvValidErrors(redirectAttributes);
		}
		
		applicantService.addApplicantCv(cv);
		return successOnlineCv(redirectAttributes, "ocv.successCv");
	}
	
	@GetMapping(Routes.EDIT)
	public String getOnlineCvEdit(@RequestParam String operationType, @RequestParam Long id, Model model) throws WorkstocksBusinessException {
		CvOperatonType opType = CvOperatonType.valueOf(operationType.toUpperCase());
		
		switch (opType) {
		case QUALIFICATION:
			model.addAttribute("qualification", applicantService.findQualificationById(id));
			break;
		case EXPERIENCE:
			model.addAttribute("experience", applicantService.findExperienceById(id));
			break;
		case CERTIFICATE:
			model.addAttribute("certificate", applicantService.findCertificateById(id));
			break;
		case SKILL:
			model.addAttribute("skill", applicantService.findSkillById(id));
			break;			
		default:
			throw new WorkstocksBusinessException("Operation not supported");
		}
		
		return Templates.APPLICANT_PROFILE.getTemplate();
	}
	
	@PostMapping(Routes.EDIT_QUALIFICATION)
	public String editApplicantQualification(@Valid @ModelAttribute("qualification") QualificationDto qualificationDto, Errors errors, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			return Templates.APPLICANT_PROFILE.getTemplate();
		}
		
		applicantService.modifyQualification(qualificationDto);
		return successOnlineCv(redirectAttributes, "ocv.updateQualification");
	}
	
	@PostMapping(Routes.EDIT_EXPERIENCE)
	public String editApplicantExperience(@Valid @ModelAttribute("experience") ExperienceDto experienceDto, Errors errors, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			return Templates.APPLICANT_PROFILE.getTemplate();
		}
		
		applicantService.modifyExperience(experienceDto);
		return successOnlineCv(redirectAttributes, "ocv.updateExperience");
	}
	
	@PostMapping(Routes.EDIT_CERTIFICATE)
	public String editApplicantCertificate(@Valid @ModelAttribute("certificate") CertificationDto certificationDto, Errors errors, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			return Templates.APPLICANT_PROFILE.getTemplate();
		}
		
		applicantService.modifyCertificate(certificationDto);
		return successOnlineCv(redirectAttributes, "ocv.updateCertificate");
	}
	
	@PostMapping(Routes.EDIT_SKILL)
	public String editApplicantSkill(@Valid @ModelAttribute("skill") SkillDto skillDto, Errors errors, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		if (errors.hasErrors()) {
			return Templates.APPLICANT_PROFILE.getTemplate();
		}
		
		applicantService.modifySkill(skillDto);
		return successOnlineCv(redirectAttributes, "ocv.updateSkill");
	}
	
	@GetMapping(Routes.DELETE)
	public String onlineApplicantCvDelete(@RequestParam String operationType, @RequestParam Long id, RedirectAttributes redirectAttributes) throws WorkstocksBusinessException {
		CvOperatonType opType = CvOperatonType.valueOf(operationType.toUpperCase());
		
		switch (opType) {
		case QUALIFICATION:
			applicantService.deleteQualificationById(id);
			return successOnlineCv(redirectAttributes, "ocv.deleteQualification");
		case EXPERIENCE:
			applicantService.deleteExperienceById(id);
			return successOnlineCv(redirectAttributes, "ocv.deleteExperience");
		case CERTIFICATE:
			applicantService.deleteCertificateById(id);
			return successOnlineCv(redirectAttributes, "ocv.deleteCertificate");
		case SKILL:
			applicantService.deleteSkillById(id);
			return successOnlineCv(redirectAttributes, "ocv.deleteSkill");		
		default:
			throw new WorkstocksBusinessException("Operation not supported");
		}

	}
	
	private String handlePopulateOnlineCvValidErrors(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute("msgError", true);
		return "redirect:"+Routes.ROOT_ONLINE_CV;
	}
	
	private String successOnlineCv(RedirectAttributes redirectAttributes, String successMsg) {
		redirectAttributes.addFlashAttribute("msgSuccess", successMsg);
		return "redirect:"+Routes.ROOT_ONLINE_CV;
	}

}
