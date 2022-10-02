package it.workstocks.repository.custom;

import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.job.JobOffer;

public interface IJobOfferDao {

	PaginatedEntityResponse<JobOffer> searchJobOffer(PaginatedRequest request);

}
