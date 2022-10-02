package it.workstocks.entity.job;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;

import org.springframework.data.annotation.Transient;

import it.workstocks.entity.BaseEntity;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.company.WorkingPlace;
import it.workstocks.entity.enums.ContractType;
import it.workstocks.entity.enums.Gender;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.entity.user.applicant.curricula.Skill;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class JobOffer extends BaseEntity<Long> {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition="TEXT")
	private String description;

	@Column(nullable = false)
	private LocalDate dueDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ContractType contractType;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false)
	private Integer minSalary;

	@Column(nullable = false)
	private Integer maxSalary;
	
	@Column(nullable = false)
	private Short experience;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Company company;

	@ManyToOne
	@JoinColumn(nullable = false)
	private WorkingPlace workingPlace;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "job_offer_id")
	private Set<Skill> skillsList = new LinkedHashSet<>();
	
	@ManyToMany
	@JoinTable(name = "favourites")
	private Set<Applicant> favouritesApplicant = new LinkedHashSet<>();
	
	@Transient
	public Set<String> getSkills() {
		if (this.getSkillsList() != null && !this.getSkillsList().isEmpty()) {
			Set<String> skills = new HashSet<>();

			for (Skill skill : this.getSkillsList()) {
				skills.add(skill.getName());
			}
			
			return skills;
		} else {
			return null;
		}
	}
	
	@PreRemove
	private void removeFavouritesFromJobOffer() {
	    this.favouritesApplicant.clear();
	}
}
