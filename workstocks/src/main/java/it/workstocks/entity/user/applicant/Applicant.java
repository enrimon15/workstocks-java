package it.workstocks.entity.user.applicant;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import it.workstocks.entity.Address;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.enums.Gender;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.entity.user.User;
import it.workstocks.entity.user.applicant.curricula.Certification;
import it.workstocks.entity.user.applicant.curricula.Experience;
import it.workstocks.entity.user.applicant.curricula.Qualification;
import it.workstocks.entity.user.applicant.curricula.Skill;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Applicant extends User {

	private String jobTitle;
	
	private String phoneNumber;

	@Column(columnDefinition="TEXT")
	private String overview;

	private String website;

	private Integer minimumExpectedSalary;

	@Embedded
	private Address address;

	@Lob
	@Basic(fetch=FetchType.LAZY)
	private byte[] curricula;

	private LocalDate dateOfBirth;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@OneToMany(mappedBy="applicant", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
	private Set<Skill> skills = new LinkedHashSet<>();

	@OneToMany(mappedBy="applicant", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
	@OrderBy("date DESC")
	private Set<Certification> certifications = new LinkedHashSet<>();

	@OneToMany(mappedBy="applicant", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
	@OrderBy("startDate DESC")
	private Set<Qualification> qualifications = new LinkedHashSet<>();

	@OneToMany(mappedBy = "applicant", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
	@OrderBy("startDate DESC")
	private Set<Experience> experiences = new LinkedHashSet<>();

	@ManyToMany(mappedBy = "favouritesApplicant")
	private Set<JobOffer> favouritesJob = new LinkedHashSet<>();
	
	@ManyToMany
	@JoinTable(name = "job_alerts")
	private Set<Company> jobAlertCompanies = new LinkedHashSet<>();
}
