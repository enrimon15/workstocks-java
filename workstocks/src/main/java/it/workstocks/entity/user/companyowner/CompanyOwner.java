package it.workstocks.entity.user.companyowner;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import it.workstocks.entity.company.Company;
import it.workstocks.entity.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class CompanyOwner extends User {
	@OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
	private Company company;
}
