package it.workstocks.entity.application;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import it.workstocks.entity.BaseEntity;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.entity.user.applicant.Applicant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class JobApplication extends BaseEntity<JobApplicationKey> {

	@EmbeddedId
	private JobApplicationKey id;
	
	@ManyToOne
	@MapsId("applicantId")
	@JoinColumn(name = "applicant_id")
	private Applicant applicant;
	
	@ManyToOne
	@MapsId("jobOfferId")
	@JoinColumn(name = "job_offer_id")
	private JobOffer jobOffer;
	
	public void setJobOfferFromDto(Long id) {
		if (this.jobOffer == null) {
			this.jobOffer = new JobOffer();
		}
		this.jobOffer.setId(id);
	}
	
	public void setApplicantFromDto(Long id) {
		if (this.applicant == null) {
			this.applicant = new Applicant();
		}
		this.applicant.setId(id);
	}

}
