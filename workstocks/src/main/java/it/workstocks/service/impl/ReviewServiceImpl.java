package it.workstocks.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.review.ReviewDto;
import it.workstocks.dto.review.ReviewKeyDto;
import it.workstocks.entity.pojo.ReviewAverageAndCountPojo;
import it.workstocks.entity.review.Review;
import it.workstocks.entity.review.ReviewKey;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.ReviewRepository;
import it.workstocks.service.ReviewService;
import it.workstocks.utils.AuthUtility;
import it.workstocks.utils.NumberUtils;

@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private EntityMapper mapper;
	
	private Review findOptionalReview(ReviewKey id) throws WorkstocksBusinessException {
		Optional<Review> findReview = reviewRepository.findById(id);
		if (findReview.isPresent()) {
			if (!AuthUtility.compareCurrentUser(id.getApplicantId())) {
				throw new AccessDeniedException("User unauthorized to get review");
			}
			return findReview.get();
		} else {
			return null;
		}
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void addOrUpdateReview(ReviewDto dto) throws WorkstocksBusinessException {
		Review review = findOptionalReview(mapper.toEntity(dto.getId()));;
		if (review != null) {
			review.setRating(dto.getRating());
			reviewRepository.save(review);
		} else {
			// not found --> create new one
			Review newReview = mapper.toEntity(dto);
			newReview.setApplicantFromDto(dto.getId().getApplicantId());
			newReview.setCompanyFromDto(dto.getId().getCompanyId());
			reviewRepository.save(newReview);
		}
	}

	@Override
	public ReviewAverageAndCountPojo findAverageRatingByCompanyId(Long companyId) throws WorkstocksBusinessException {
		return reviewRepository.getCompanyRatingAverage(companyId);
	}

	@Override
	public ReviewDto findById(ReviewKeyDto key) throws WorkstocksBusinessException {
		if (key == null || NumberUtils.isNotPositive(key.getApplicantId())
				|| NumberUtils.isNotPositive(key.getCompanyId())) {
			throw new WorkstocksBusinessException(
					String.format("ApplicantId and CompanyId are mandatory in composite key %s", ReviewKeyDto.class));
		}

		Review result = findOptionalReview(mapper.toEntity(key));
		return result != null ? mapper.toDto(result) : null;
	}

}
