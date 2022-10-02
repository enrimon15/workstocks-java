package it.workstocks.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.user.applicant.curricula.Qualification;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, Long> {
	Set<Qualification> findAllByApplicantIdOrderByStartDateDesc(Long applicantId);
}
