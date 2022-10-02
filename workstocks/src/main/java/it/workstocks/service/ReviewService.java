package it.workstocks.service;

import it.workstocks.dto.review.ReviewDto;
import it.workstocks.entity.pojo.ReviewAverageAndCountPojo;
import it.workstocks.exception.WorkstocksBusinessException;

public interface ReviewService {
	
	void addReview(ReviewDto dto, Long companyId) throws WorkstocksBusinessException;
	void updateReview(ReviewDto dto, Long companyId) throws WorkstocksBusinessException;
	ReviewAverageAndCountPojo findAverageRatingByCompanyId(Long companyId) throws WorkstocksBusinessException;
	ReviewDto findByCompanyId(Long companyId) throws WorkstocksBusinessException;
}
