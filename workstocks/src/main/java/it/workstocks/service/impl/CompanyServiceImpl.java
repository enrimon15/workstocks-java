package it.workstocks.service.impl;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.user.company.CompanyDto;
import it.workstocks.dto.user.company.SimpleCompanyDto;
import it.workstocks.dto.user.company.WorkingPlaceDto;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.company.Company;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.CompanyRepository;
import it.workstocks.repository.ReviewRepository;
import it.workstocks.repository.WorkingPlaceRepository;
import it.workstocks.repository.custom.ICompanyDao;
import it.workstocks.service.CompanyService;
import it.workstocks.utils.ErrorUtils;
import it.workstocks.utils.Translator;

@Service
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private WorkingPlaceRepository workingPlaceRepository;

	@Autowired
	private CompanyRepository companyRepository;

	@Autowired
	private EntityMapper mapper;

	@Autowired
	private ICompanyDao companyDao;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private Translator translator;
	
	@Override
	public void checkCompanyExistence(Long companyId) throws WorkstocksBusinessException {
		boolean exists = companyRepository.existsById(companyId);
		if (!exists) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.NOT_FOUND, new Object[] {"company", companyId}),HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public Set<WorkingPlaceDto> findWorkingPlacesByCompanyId(Long id) throws WorkstocksBusinessException {
		return mapper.toDtoWorkingPlaceCollection(workingPlaceRepository.findByCompanyId(id));
	}


	@Override
	public PaginatedDtoResponse<SimpleCompanyDto> searchCompanies(PaginatedRequest request) {
		PaginatedEntityResponse<Company> daoPaginatedResult = companyDao.searchCompany(request);
	
		PaginatedDtoResponse<SimpleCompanyDto> dtoReponse = new PaginatedDtoResponse<>();	
		dtoReponse.setElements(mapper.toDtoSet(daoPaginatedResult.getElements()));
		dtoReponse.setResponse(daoPaginatedResult.getReponse());		
		

		return dtoReponse;
	}
	
	@Override
	public long countAllCompany() {
		return companyRepository.count();
	}
	
	@Override
	public Set<SimpleCompanyDto> findMostRatedCompanies(Integer limit) {
		Page<Company> companyPage = reviewRepository.getPopularCompanies(PageRequest.of(0, limit));
		return mapper.toDtoSet(new LinkedHashSet<>(companyPage.getContent()));		
	}

	@Override
	public CompanyDto findById(Long companyId) throws WorkstocksBusinessException {
		Optional<Company> company = companyRepository.findById(companyId);
		if (company.isEmpty()) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.NOT_FOUND, new Object[] {"company", companyId}),HttpStatus.NOT_FOUND);
		} else {
			return mapper.toDto(company.get());
		}
	}
	
	@Override
	public byte[] findPhotoById(Long companyId) throws WorkstocksBusinessException {
		Optional<Company> company = companyRepository.findById(companyId);
		if (company.isEmpty()) {
			throw new WorkstocksBusinessException(translator.toLocale(ErrorUtils.NOT_FOUND, new Object[] {"company", companyId}),HttpStatus.NOT_FOUND);
		} else {
			return company.get().getCompanyOwner().getAvatar();
		}
	}
	
	

}
