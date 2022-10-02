package it.workstocks.entity.company;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import it.workstocks.entity.BaseEntity;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.entity.user.companyowner.CompanyOwner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Company extends BaseEntity<Long> {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(unique = true, nullable = false)
	private Long vatNumber;
	
	@Column(nullable = false)
	private String companyForm;
	
	@Column(columnDefinition="TEXT")
	private String overview;
	
	private Integer employeesNumber;
	
	@Column(nullable = false)
	private Integer foundationYear;
	
	private String website;
	
	private String slogan;
	
	private String telephone;

	@OneToOne(mappedBy = "company")
	private CompanyOwner companyOwner;
	
	@OneToMany(mappedBy = "company", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
	private Set<JobOffer> jobOffers = new LinkedHashSet<>();
		
	@OneToMany(mappedBy="company", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
	private Set<WorkingPlace> workingPlaces = new LinkedHashSet<>();
	
	@Transient
	public WorkingPlace getMainWorkingPlace() {
		if (this.workingPlaces == null) return null;
		return this.workingPlaces.stream()
                .filter(wp -> wp.isMainWorkingPlace() == true)
                .findAny()
                .orElse(null);
	}
}
