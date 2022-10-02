package it.workstocks.service;

import it.workstocks.dto.review.ReviewDto;
import it.workstocks.dto.review.ReviewKeyDto;
import it.workstocks.entity.pojo.ReviewAverageAndCountPojo;
import it.workstocks.exception.WorkstocksBusinessException;

public interface ReviewService {
	
	void addOrUpdateReview(ReviewDto dto) throws WorkstocksBusinessException;
	ReviewAverageAndCountPojo findAverageRatingByCompanyId(Long companyId) throws WorkstocksBusinessException;
	ReviewDto findById(ReviewKeyDto key) throws WorkstocksBusinessException;

}
