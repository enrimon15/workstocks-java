package it.workstocks.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.workstocks.entity.company.Company;
import it.workstocks.entity.pojo.ReviewAverageAndCountPojo;
import it.workstocks.entity.review.Review;
import it.workstocks.entity.review.ReviewKey;

public interface ReviewRepository extends JpaRepository<Review, ReviewKey>{
	
	@Query("SELECT new it.workstocks.entity.pojo.ReviewAverageAndCountPojo( ROUND(AVG(r.rating),1), COUNT(r) ) FROM Review r where r.id.companyId = :companyId GROUP BY r.id.companyId")
	ReviewAverageAndCountPojo getCompanyRatingAverage(Long companyId);
	
	@Query("SELECT r.company FROM Review r GROUP BY r.id.companyId ORDER BY AVG(r.rating) DESC")
	Page<Company> getPopularCompanies(Pageable pageable);

}
