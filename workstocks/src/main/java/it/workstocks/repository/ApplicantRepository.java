package it.workstocks.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.user.applicant.Applicant;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
	Applicant findByEmail(String email);
	boolean existsByIdAndFavouritesJobId(Long id, Long jobId);
	Set<Applicant> findByJobAlertCompaniesId(Long companyId);
	boolean existsByIdAndJobAlertCompaniesId(Long id, Long jobId);
}
