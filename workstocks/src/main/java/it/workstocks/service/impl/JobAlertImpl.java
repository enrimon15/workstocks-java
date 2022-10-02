package it.workstocks.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.FirebaseMessagingException;

import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.exception.WorkstocksBusinessException;
//import it.workstocks.queue.FirebaseMessagingService;
//import it.workstocks.queue.Note;
import it.workstocks.queue.QueueProducer;
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

//	@Autowired
//	private FirebaseMessagingService fb;

	@Override
	public void runJobAlert() throws WorkstocksBusinessException {
//		Set<JobOfferDto> todayJobOffer = jobOfferService.findAllJobOfferCreatedToday();
//
//		if (todayJobOffer != null && !todayJobOffer.isEmpty()) {
//			Note n = new Note();
//			n.setSubject("Nuove offerte di lavoro da non perdere");
//			n.setContent("Collegati subito su workstocks per non perdere le ultime " + todayJobOffer.size()
//					+ " offerte di lavoro pubblicate oggi!");
//
//			try {
//				fb.sendNotification(n, "topic=GOLD");
//			} catch (FirebaseMessagingException e) {
//				throw new WorkstocksBusinessException(e);
//			}
//		}

	}

	@Override
	public boolean isJobAlertApplicaredByApplicant(Long companyId, Long applicantId)
			throws WorkstocksBusinessException {
		return applicantRepository.existsByIdAndJobAlertCompaniesId(applicantId, companyId);
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public Long addOrRemoveJobAlert(Long companyId, Long applicantId, boolean isAdding)
			throws WorkstocksBusinessException {
		Applicant applicant = applicantRepository.findById(applicantId).get();

		if (isAdding) {
			if (isJobAlertApplicaredByApplicant(companyId, applicantId)) {
				throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.JOB_ALERT_ALREADY_APPLIED,
						new Long[] { applicantId, companyId }), HttpStatus.CONFLICT);
			}

			Company comp = new Company();
			comp.setId(companyId);
			applicant.getJobAlertCompanies().add(comp);
		} else {
			boolean found = applicant.getJobAlertCompanies().removeIf(comp -> comp.getId().equals(companyId));

			if (!found) {
				throw new WorkstocksBusinessException(
						translator.toLocale(ErrorUtils.JOB_ALERT_NOT_FOUND, new Long[] { applicantId, companyId }),
						HttpStatus.NOT_FOUND);
			}
		}

		applicantRepository.save(applicant);
		return companyId;
	}
}
