package it.workstocks.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.user.applicant.curricula.Experience;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long>{
	Set<Experience> findAllByApplicantIdOrderByStartDateDesc(Long applicantId);
}
