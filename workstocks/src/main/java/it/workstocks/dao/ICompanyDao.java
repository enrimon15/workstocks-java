package it.workstocks.dao;

import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.company.Company;

public interface ICompanyDao {

	PaginatedEntityResponse<Company> searchCompany(PaginatedRequest request);

}
