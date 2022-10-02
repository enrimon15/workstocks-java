package it.workstocks.service;

import java.util.Set;

import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.user.company.CompanyDto;
import it.workstocks.dto.user.company.SimpleCompanyDto;
import it.workstocks.dto.user.company.WorkingPlaceDto;
import it.workstocks.exception.WorkstocksBusinessException;

public interface CompanyService {

	Set<WorkingPlaceDto> findWorkingPlacesByCompanyId(Long id) throws WorkstocksBusinessException;
	
	PaginatedDtoResponse<SimpleCompanyDto> searchCompanies(PaginatedRequest request);
	
	Set<SimpleCompanyDto> findMostRatedCompanies(Integer limit);
	
	long countAllCompany();
	
	void checkCompanyExistence(Long companyId) throws WorkstocksBusinessException; 
	
	CompanyDto findById(Long companyId) throws WorkstocksBusinessException; 
	
	byte[] findPhotoById(Long companyId) throws WorkstocksBusinessException;
}
