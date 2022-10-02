package it.workstocks.dao;

import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.user.applicant.Applicant;

public interface IApplicantDao {

	PaginatedEntityResponse<Applicant> searchApplicant(PaginatedRequest request);

}
