package it.workstocks.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.queue.message.EmailMessageJob;
import it.workstocks.queue.message.EmailType;
import it.workstocks.queue.producer.QueueProducer;
import it.workstocks.repository.user.ApplicantRepository;
import it.workstocks.service.ApplicantService;
import it.workstocks.service.JobAlertService;
import it.workstocks.service.JobOfferService;

@Service
@Transactional(readOnly = true)
public class JobAlertImpl implements JobAlertService {

	@Autowired
	private JobOfferService jobOfferService;
	
	@Autowired
	private ApplicantService applicantService;
	
	@Autowired
	private ApplicantRepository applicantRepository;
	
	@Autowired
	private QueueProducer queueProducer;

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
		
		applicantService.getAllApplicantsJobAlertingCompany(companyId).stream().forEach(el -> {
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
	public void addOrRemoveJobAlert(Long companyId, Long applicantId, boolean isAdding) throws WorkstocksBusinessException {
		Optional<Applicant> applicantOpt = applicantRepository.findById(applicantId);
		if (applicantOpt.isEmpty()) throw new WorkstocksBusinessException("Applicant not found");
		Applicant applicant = applicantOpt.get();
		
		if (isAdding) {
			Company comp = new Company();
			comp.setId(companyId);
			applicant.getJobAlertCompanies().add(comp);
		} else {
			applicant.getJobAlertCompanies().removeIf(comp -> comp.getId().equals(companyId));
		}
		
		applicantRepository.save(applicant);
	}
}
