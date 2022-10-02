package it.workstocks.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.user.applicant.curricula.Certification;

@Repository
public interface CertificateRepository extends JpaRepository<Certification, Long> {
	Set<Certification> findAllByApplicantIdOrderByDateDesc(Long applicantId);
}
