package it.workstocks.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.job.JobOffer;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
	Page<JobOffer> findByFavouritesApplicantIdOrderByDueDateDesc(Long applicantId, Pageable pageable);
	Page<JobOffer> findByCompanyIdAndDueDateGreaterThanEqualOrderByDueDateDesc(Long companyId, LocalDate now, Pageable pageable);
	Set<JobOffer> findByCreatedAtGreaterThanEqual(LocalDateTime date);
}
