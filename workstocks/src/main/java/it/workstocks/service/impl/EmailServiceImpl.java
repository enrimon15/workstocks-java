package it.workstocks.service.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import it.workstocks.configuration.WorkstocksProperties;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.queue.message.EmailMessageJob;
import it.workstocks.repository.ApplicantRepository;
import it.workstocks.repository.JobOfferRepository;
import it.workstocks.service.EmailService;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.MailType;
import it.workstocks.utils.Translator;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
public class EmailServiceImpl implements EmailService {

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private JobOfferRepository jobOfferRepository;
	
	@Autowired
	private ApplicantRepository applicantRepository;
	
	@Autowired
	private WorkstocksProperties props;
	
	@Autowired
	private Translator translator;

	private static final String MAIL_OBJECT_APPLY_APPLICANT = "Application correctly sent";
	private static final String MAIL_OBJECT_APPLY_COMPANY = "Job offer application received";
	private static final String MAIL_OBJECT_JOB_ALERT = "New Job Offer Alert";
	private static final String MAIL_RESET_OBJECT = "Reset Password Notification";
	private static final String MAIL_OBJECT_CONTACT = "Contact Request";

	@Override
	public void sendApplicationEmails(EmailMessageJob emailMessage) throws WorkstocksBusinessException {

		JobOffer jobOffer = jobOfferRepository.findById(emailMessage.getJobOfferId()).get();
		Applicant applicant = applicantRepository.findById(emailMessage.getApplicantId()).get();

		String companyEmail = jobOffer.getCompany().getCompanyOwner().getEmail();
		String userEmail = applicant.getEmail();
		String name = applicant.getName();
		String surname = applicant.getSurname();

		Map<String, String> param = new HashMap<>();
		param.put("name", name);
		param.put("surname", surname);
		param.put("jobOfferName", jobOffer.getTitle());
		param.put("companyName", jobOffer.getCompany().getName());
		param.put("url", props.getSite().getUrl() + "/v1/job-offers/" + emailMessage.getJobOfferId());

		sendMail(userEmail, MAIL_OBJECT_APPLY_APPLICANT, param, MailType.APPLICATION_APPLICANT);
		sendMail(companyEmail, MAIL_OBJECT_APPLY_COMPANY, param, MailType.APPLICATION_COMPANY);

	}
	
	@Override
	public void sendJobAlert(EmailMessageJob emailMessage) throws WorkstocksBusinessException {

		JobOffer jobOffer = jobOfferRepository.findById(emailMessage.getJobOfferId()).get();
		Applicant applicant = applicantRepository.findById(emailMessage.getApplicantId()).get();

		String userEmail = applicant.getEmail();
		String name = applicant.getName();
		String surname = applicant.getSurname();

		Map<String, String> param = new HashMap<>();
		param.put("name", name);
		param.put("surname", surname);
		param.put("jobOfferName", jobOffer.getTitle());
		param.put("companyName", jobOffer.getCompany().getName());
		param.put("url", props.getSite().getUrl() + "/v1/job-offers/" + emailMessage.getJobOfferId());
		
		sendMail(userEmail, MAIL_OBJECT_JOB_ALERT, param, MailType.JOB_ALERT);
	}
	
	@Override
	public void sendResetToken(String emailTo, String token) throws WorkstocksBusinessException {
		Map<String, String> param = new HashMap<>();
		param.put("url", props.getSite().getUrl() + "/v1/auth/reset-password" + "?token=" + token);
		sendMail(emailTo, MAIL_RESET_OBJECT, param, MailType.RESET_PASSWORD);
	}
	
	@Override
	public void sendContactRequest(String emailTo, String emailFrom, String sourceTextMessage) throws WorkstocksBusinessException {
		Map<String, String> param = new HashMap<>();
		param.put("url", props.getSite().getUrl());
		param.put("emailFrom", emailFrom);
		param.put("message", sourceTextMessage);
		sendMail(emailTo, MAIL_OBJECT_CONTACT, param, MailType.CONTACT);
	}

	private void sendMail(String emailTo, String object, Map<String, String> param, MailType mailType) throws WorkstocksBusinessException {
		Context context = new Context();
		for (String key : param.keySet()) {
			context.setVariable(key, param.get(key));
		}

		try {
			String process = templateEngine.process(mailType.getTemplate(), context);
			javax.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
			helper.setSubject(object);
			helper.setText(process, true);
			helper.setTo(emailTo);
			helper.setFrom("workstocksMail", "Work-Stocks");
			javaMailSender.send(mimeMessage);
		} catch (MessagingException | UnsupportedEncodingException e) {
			log.error("Unable to send email " + mailType.name() + ": " + e);
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.EMAIL, new Object[] {mailType.name()}), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
