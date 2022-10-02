package it.workstocks.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.application.JobApplication;
import it.workstocks.entity.application.JobApplicationKey;
import it.workstocks.entity.job.JobOffer;

@Repository
public interface ApplicationRepository extends JpaRepository<JobApplication, JobApplicationKey>{
	Page<JobApplication> findByApplicantIdOrderByCreatedAtDesc(Long applicantId, Pageable pageable);
	Page<JobApplication> findByJobOfferIdOrderByCreatedAtDesc(Long jobOfferId, Pageable pageable);
	Integer countByJobOfferId(Long jobId);
	void deleteByJobOfferId(Long jobOfferId);
	
	@Query("SELECT app.jobOffer FROM JobApplication app GROUP BY app.jobOffer.id ORDER BY COUNT(app) DESC")
	Page<JobOffer> findPopularJobOffer(Pageable pageable);
}
