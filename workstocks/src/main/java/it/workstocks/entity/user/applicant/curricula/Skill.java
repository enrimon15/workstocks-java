package it.workstocks.entity.user.applicant.curricula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import it.workstocks.entity.BaseEntity;
import it.workstocks.entity.enums.Assestment;
import it.workstocks.entity.enums.SkillType;
import it.workstocks.entity.user.applicant.Applicant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Skill extends BaseEntity<Long> {

	@Id
	@GeneratedValue
	private Long id;

	@Enumerated(EnumType.STRING)
	private Assestment assestment;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Applicant applicant;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SkillType skillType;
	
	@Column(nullable = false)
	private String name;
	

}
