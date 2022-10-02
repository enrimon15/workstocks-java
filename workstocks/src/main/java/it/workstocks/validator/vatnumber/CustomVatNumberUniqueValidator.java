package it.workstocks.validator.vatnumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.workstocks.repository.CompanyRepository;
import it.workstocks.security.Roles;
import it.workstocks.utils.AuthUtility;

@Component
public class CustomVatNumberUniqueValidator implements ConstraintValidator<VatNumberUniqueConstraint, Long> {

	@Autowired 
	private CompanyRepository companyRepository;
	
	@Override
	public boolean isValid(Long vatNumber, ConstraintValidatorContext context) {
		
		boolean duplicateVatNumber = companyRepository.existsByVatNumber(vatNumber);
		
		if (AuthUtility.getAuth() != null && AuthUtility.hasRole(Roles.COMPANY_OWNER)) { // modifica profilo
			if (vatNumber != null && !vatNumber.equals(AuthUtility.getCurrentCompanyOwner().getCompany().getVatNumber())) {
				return !duplicateVatNumber;
			}
		} else { // registrazione
			if (vatNumber != null) {
				return !duplicateVatNumber;
			}
		}
		
		return true;
	}
	
}
