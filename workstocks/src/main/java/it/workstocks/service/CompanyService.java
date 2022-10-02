package it.workstocks.service;

import java.util.Set;

import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.user.company.CompanyDto;
import it.workstocks.dto.user.company.SimpleCompanyOwnerDto;
import it.workstocks.dto.user.company.WorkingPlaceDto;
import it.workstocks.exception.WorkstocksBusinessException;

public interface CompanyService {

	SimpleCompanyOwnerDto findCompanyOwnerById(Long id) throws WorkstocksBusinessException;

	void updateCompanyOwnerProfile(SimpleCompanyOwnerDto companyOwnerDto) throws WorkstocksBusinessException;

	Set<WorkingPlaceDto> findWorkingPlacesByCompanyId(Long id) throws WorkstocksBusinessException;

	void insertOrUpdateWorkingPlace(WorkingPlaceDto workingPlaceDto) throws WorkstocksBusinessException;

	WorkingPlaceDto findWorkingPlaceById(Long id) throws WorkstocksBusinessException;

	void deleteWorkingPlaceById(Long id) throws WorkstocksBusinessException;
	
	PaginatedDtoResponse<CompanyDto> searchCompanies(PaginatedRequest request) throws WorkstocksBusinessException;
	
	Set<CompanyDto> findMostRatedCompanies() throws WorkstocksBusinessException;
	
	long countAllCompany() throws WorkstocksBusinessException;

	boolean isModifyMainWorkingPlace(WorkingPlaceDto workingPlaceDto) throws WorkstocksBusinessException;
	
	

}
