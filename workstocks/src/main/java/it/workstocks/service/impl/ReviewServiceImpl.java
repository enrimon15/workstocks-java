package it.workstocks.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.review.ReviewDto;
import it.workstocks.entity.pojo.ReviewAverageAndCountPojo;
import it.workstocks.entity.review.Review;
import it.workstocks.entity.review.ReviewKey;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.ReviewRepository;
import it.workstocks.service.ReviewService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;

@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private Translator translator;

	@Autowired
	private EntityMapper mapper;
	
	private Review findOptionalReview(ReviewKey id) throws WorkstocksBusinessException {
		Optional<Review> findReview = reviewRepository.findById(id);
		if (findReview.isPresent()) {
			return findReview.get();
		} else {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.REVIEW_NOT_FOUND, null),HttpStatus.NOT_FOUND);
		}
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addReview(ReviewDto dto, Long companyId) throws WorkstocksBusinessException {
		ReviewKey key = buildReviewKey(companyId, AuthUtility.getCurrentApplicant().getId());
		Optional<Review> findReview = reviewRepository.findById(key);
		if (findReview.isPresent()) {
			throw new WorkstocksBusinessException("Review is already present", HttpStatus.CONFLICT);
		}
		
		Review newReview = mapper.toEntity(dto);
		newReview.setApplicantFromDto(AuthUtility.getCurrentApplicant().getId());
		newReview.setCompanyFromDto(companyId);
		reviewRepository.save(newReview);
	}
	
	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void updateReview(ReviewDto dto, Long companyId) throws WorkstocksBusinessException {
		ReviewKey key = buildReviewKey(companyId, AuthUtility.getCurrentApplicant().getId());
		Review review = findOptionalReview(key);
		review.setRating(dto.getRating());
		reviewRepository.save(review);
	}

	@Override
	public ReviewAverageAndCountPojo findAverageRatingByCompanyId(Long companyId) throws WorkstocksBusinessException {
		return reviewRepository.getCompanyRatingAverage(companyId);
	}

	@Override
	public ReviewDto findByCompanyId(Long companyId) throws WorkstocksBusinessException {
		ReviewKey key = buildReviewKey(companyId, AuthUtility.getCurrentApplicant().getId());
		return mapper.toDto(findOptionalReview(key));
	}
	
	private ReviewKey buildReviewKey(Long companyId, Long applicantId) {
		ReviewKey key = new ReviewKey();
		key.setApplicantId(AuthUtility.getCurrentApplicant().getId());
		key.setCompanyId(companyId);
		return key;
	}

}
