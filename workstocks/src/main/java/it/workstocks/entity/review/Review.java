package it.workstocks.entity.review;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import it.workstocks.entity.BaseEntity;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.user.applicant.Applicant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Review extends BaseEntity<ReviewKey> {
	
	@EmbeddedId
	private ReviewKey id;
	
	@Column(nullable = false)
	private int rating;
	
	@ManyToOne
	@MapsId("applicantId")
	@JoinColumn(name = "applicant_id")
	private Applicant applicant;
	
	@ManyToOne
	@MapsId("companyId")
	@JoinColumn(name = "company_id")
	private Company company;
	
	public void setCompanyFromDto(Long id) {
		if (this.company == null) {
			this.company = new Company();
		}
		this.company.setId(id);
	}
	
	public void setApplicantFromDto(Long id) {
		if (this.applicant == null) {
			this.applicant = new Applicant();
		}
		this.applicant.setId(id);
	}

}
