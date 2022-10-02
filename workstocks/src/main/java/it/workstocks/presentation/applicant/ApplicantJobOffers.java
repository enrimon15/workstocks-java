package it.workstocks.presentation.applicant;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.workstocks.dto.job.JobOfferDto;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.presentation.Routes;
import it.workstocks.queue.message.EmailMessageJob;
import it.workstocks.queue.message.EmailType;
import it.workstocks.queue.producer.QueueProducer;
import it.workstocks.service.ApplicationService;
import it.workstocks.service.JobAlertService;
import it.workstocks.service.JobOfferService;
import it.workstocks.utils.AuthUtility;

@Controller
@RequestMapping(path = Routes.ROOT_APPLICANT)
public class ApplicantJobOffers {

	@Autowired
	private JobOfferService jobOfferService;
	
	@Autowired
	private JobAlertService jobAlertService;
	
	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private QueueProducer producer;

	@GetMapping(Routes.JOB_OFFER_FAVORITE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> addOrRemoveFavorite(@RequestParam("jobId") Long jobId) throws WorkstocksBusinessException {
		boolean isFavorite = jobOfferService.isFavoriteForApplicant(jobId, AuthUtility.getCurrentApplicant().getId());
		jobOfferService.addOrRemoveApplicantFavorite(isFavorite, AuthUtility.getCurrentApplicant().getId(), jobId);
		
		Map<String, Object> result = new HashMap<>();
		result.put("isFavorite", isFavorite);
		return ResponseEntity.ok(result);
	}

	@GetMapping(Routes.JOB_OFFER_APPLY)
	@ResponseBody
	public ResponseEntity<Boolean> applyJobOffer(@RequestParam("id") Long id) throws WorkstocksBusinessException {
		JobOfferDto jobOffer = jobOfferService.findById(id);
		if (jobOffer.getDueDate().isBefore(LocalDate.now())) {
			throw new WorkstocksBusinessException("Offerta scaduta");
		}

		applicationService.applyForApplicant(id, AuthUtility.getCurrentApplicant().getId());

		EmailMessageJob m = new EmailMessageJob();
		m.setEmailType(EmailType.APPLICATION);
		m.setApplicantId(AuthUtility.getCurrentApplicant().getId());
		m.setJobOfferId(id);
		producer.send(m);

		return ResponseEntity.ok(true);
	}
	
	@GetMapping(Routes.JOB_ALERT)
	@ResponseBody
	public ResponseEntity<Map<String, String>> addOrRemoveJobAlert(@RequestParam("companyId") Long companyId, @RequestParam("opType") String opType) throws WorkstocksBusinessException {
		boolean isAdding = opType.equalsIgnoreCase("add") ? true : false;	
		jobAlertService.addOrRemoveJobAlert(companyId, AuthUtility.getCurrentApplicant().getId(), isAdding);

		Map<String, String> result = new HashMap<>();
		result.put("opType", opType);

		return ResponseEntity.ok(result);
	}		

}
