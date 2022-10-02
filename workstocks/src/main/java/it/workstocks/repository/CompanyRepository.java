package it.workstocks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.company.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
	boolean existsByVatNumber(Long vatNumber);
}
