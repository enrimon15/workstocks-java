package it.workstocks.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.user.companyowner.CompanyOwner;

@Repository
public interface CompanyOwnerRepository extends JpaRepository<CompanyOwner, Long>{

}
