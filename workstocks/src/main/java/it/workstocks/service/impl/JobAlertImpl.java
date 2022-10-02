package it.workstocks.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.queue.QueueProducer;
import it.workstocks.queue.message.EmailMessageJob;
import it.workstocks.queue.message.EmailType;
import it.workstocks.repository.ApplicantRepository;
import it.workstocks.service.JobAlertService;
import it.workstocks.service.JobOfferService;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;

@Service
@Transactional(readOnly = true)
public class JobAlertImpl implements JobAlertService {

	@Autowired
	private JobOfferService jobOfferService;
	
	@Autowired
	private ApplicantRepository applicantRepository;
	
	@Autowired
	private QueueProducer queueProducer;
	
	@Autowired
	private Translator translator;

	@Override
	public void runJobAlert() throws WorkstocksBusinessException {

		Map<Long, Set<Long>> applicantsInscribedForCompany = new HashMap<>();

		Set<JobOfferDto> todayJobOffer = jobOfferService.findAllJobOfferCreatedToday();

		EmailMessageJob em = new EmailMessageJob();
		em.setEmailType(EmailType.JOB_ALERT);
		//Prendo Ogni nuova offerta
		for (JobOfferDto jod : todayJobOffer) {
			em.setJobOfferId(jod.getId());
			//recupero la lista degli utenti in job alert per una certa azienda
			Set<Long> applicants = applicantsInscribedForCompany.computeIfAbsent(jod.getCompany().getId(), k -> getJobAlertApplicantsForCompany(k));
			
			//invio una mail ad ogni utente
			for(Long applicant: applicants) {
				em.setApplicantId(applicant);
				queueProducer.send(em);
			}
		}
	}
	
	private Set<Long> getJobAlertApplicantsForCompany(Long companyId) {
		Set<Long> applicants = new HashSet<>();
		
		applicantRepository.findByJobAlertCompaniesId(companyId).stream().forEach(el -> {
			applicants.add(el.getId());
		});
				
		return applicants;
	}

	@Override
	public boolean isJobAlertApplicaredByApplicant(Long companyId, Long applicantId) throws WorkstocksBusinessException {
		return applicantRepository.existsByIdAndJobAlertCompaniesId(applicantId, companyId);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public Long addOrRemoveJobAlert(Long companyId, Long applicantId, boolean isAdding) throws WorkstocksBusinessException {
		Applicant applicant = applicantRepository.findById(applicantId).get();
		
		if (isAdding) {
			if (isJobAlertApplicaredByApplicant(companyId, applicantId)) {
				throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.JOB_ALERT_ALREADY_APPLIED, new Long[] {applicantId, companyId}),HttpStatus.CONFLICT);
			}
			
			Company comp = new Company();
			comp.setId(companyId);
			applicant.getJobAlertCompanies().add(comp);
		} else {
			boolean found = applicant.getJobAlertCompanies().removeIf(comp -> comp.getId().equals(companyId));
			
			if(!found) {
				throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.JOB_ALERT_NOT_FOUND, new Long[] {applicantId, companyId}),HttpStatus.NOT_FOUND);
			}
		}
		
		applicantRepository.save(applicant);
		return companyId;
	}
}
