package it.workstocks.repository.user;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.entity.user.applicant.curricula.Qualification;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
	Applicant findByEmail(String email);
	Set<Qualification> findAllQualificationsById(Long id);
	boolean existsByIdAndFavouritesJobId(Long id, Long jobId);
	Set<Applicant> findByJobAlertCompaniesId(Long companyId);
	boolean existsByIdAndJobAlertCompaniesId(Long id, Long jobId);
}
