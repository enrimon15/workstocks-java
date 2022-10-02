package it.workstocks.entity.company;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import it.workstocks.entity.Address;
import it.workstocks.entity.BaseEntity;
import it.workstocks.entity.enums.WorkingPlaceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class WorkingPlace extends BaseEntity<Long>{
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private WorkingPlaceType workingPlaceType;
	
	@Embedded
	@Column(nullable = false)
	private Address address;
	
	private boolean mainWorkingPlace = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Company company;

}
