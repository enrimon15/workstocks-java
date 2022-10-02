package it.workstocks.entity.user.applicant.curricula;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import it.workstocks.entity.BaseEntity;
import it.workstocks.entity.user.applicant.Applicant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Experience extends BaseEntity<Long>{
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Column(nullable = false)
	private String jobPosition;
	
	@Column(nullable = false)
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private boolean inProgress;
	
	@Column(nullable = false)
	private String company;
	
	private String description;
	
	@PrePersist
	@PreUpdate
	private void checkInProgress() {
		this.inProgress = endDate == null ? true : false;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Applicant applicant;
	

}
