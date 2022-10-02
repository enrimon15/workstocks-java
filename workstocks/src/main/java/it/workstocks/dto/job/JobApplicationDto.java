package it.workstocks.dto.job;

import it.workstocks.dto.BaseDto;
import it.workstocks.dto.user.applicant.SimpleApplicantDto;
import it.workstocks.entity.application.JobApplicationKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationDto extends BaseDto<JobApplicationKey> {
	private JobApplicationKey id;
	private SimpleApplicantDto applicant;
	private JobOfferDto jobOffer;
}
