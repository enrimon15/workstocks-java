package it.workstocks.service.impl;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.dao.ICompanyDao;
import it.workstocks.dto.mapper.EntityMapper;
import it.workstocks.dto.pagination.PaginatedDtoResponse;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.dto.user.company.CompanyDto;
import it.workstocks.dto.user.company.SimpleCompanyOwnerDto;
import it.workstocks.dto.user.company.WorkingPlaceDto;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.company.Company;
import it.workstocks.entity.company.WorkingPlace;
import it.workstocks.entity.user.companyowner.CompanyOwner;
import it.workstocks.exception.WorkstocksBusinessException;
import it.workstocks.repository.CompanyRepository;
import it.workstocks.repository.ReviewRepository;
import it.workstocks.repository.WorkingPlaceRepository;
import it.workstocks.repository.user.CompanyOwnerRepository;
import it.workstocks.service.CompanyService;
import it.workstocks.utils.AuthUtility;

@Service
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private CompanyOwnerRepository companyOwnerRepository;

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

	private CompanyOwner findOptionalCompanyOwner(Long id) throws WorkstocksBusinessException {
		Optional<CompanyOwner> companyOwner = companyOwnerRepository.findById(id);
		if (companyOwner.isPresent()) {
			return companyOwner.get();
		} else {
			throw new WorkstocksBusinessException("Company Owner not found");
		}
	}

	@Override
	public SimpleCompanyOwnerDto findCompanyOwnerById(Long id) throws WorkstocksBusinessException {
		CompanyOwner companyOwner = findOptionalCompanyOwner(id);
		SimpleCompanyOwnerDto companyOwnerDto = mapper.toSimpleDto(companyOwner);
		companyOwnerDto.getCompany().setMainWorkingPlace(mapper.toDto(companyOwner.getCompany().getMainWorkingPlace()));

		return companyOwnerDto;
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void updateCompanyOwnerProfile(SimpleCompanyOwnerDto companyOwnerDto) throws WorkstocksBusinessException {
		CompanyOwner companyOwner = findOptionalCompanyOwner(companyOwnerDto.getId());
		mapper.updateCompanyOwner(companyOwnerDto, companyOwner);
		if (companyOwnerDto.getAvatar() != null) companyOwner.setAvatar(companyOwnerDto.getAvatar());

		WorkingPlace wp = companyOwner.getCompany().getMainWorkingPlace();
		wp.setAddress(mapper.toEntity(companyOwnerDto.getCompany().getMainWorkingPlace().getAddress()));
		wp.setWorkingPlaceType(companyOwnerDto.getCompany().getMainWorkingPlace().getWorkingPlaceType());

		companyOwner = companyOwnerRepository.save(companyOwner);
		
		AuthUtility.renewAuthenticationPrincipal(companyOwner);

	}

	@Override
	public Set<WorkingPlaceDto> findWorkingPlacesByCompanyId(Long id) throws WorkstocksBusinessException {
		return mapper.toDtoWorkingPlaceCollection(workingPlaceRepository.findByCompanyId(id));
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void insertOrUpdateWorkingPlace(WorkingPlaceDto workingPlaceDto) throws WorkstocksBusinessException {
		WorkingPlace workingPlace = mapper.toEntity(workingPlaceDto);
		Long companyId = AuthUtility.getCurrentCompanyOwner().getCompany().getId();

		if (workingPlaceDto.getId() != null) {
			WorkingPlace wp = getOptionalWorkingPlace(workingPlaceDto.getId());
			if (wp.isMainWorkingPlace() && !workingPlaceDto.isMainWorkingPlace()) {
				throw new WorkstocksBusinessException("Impossible to remove main working place before insert new one");
			}
			workingPlace.setCompany(wp.getCompany());
		} else {
			Company company = new Company();
			company.setId(companyId);
			workingPlace.setCompany(company);
		}

		if (workingPlaceDto.isMainWorkingPlace()) {
			Set<WorkingPlace> companyWorkingPlaces = workingPlaceRepository.findByCompanyIdAndMainWorkingPlace(companyId, true);
			for (WorkingPlace w : companyWorkingPlaces) {
				w.setMainWorkingPlace(false);
			}

			workingPlaceRepository.saveAll(companyWorkingPlaces);
		}
		
		workingPlaceRepository.save(workingPlace);

	}

	private WorkingPlace getOptionalWorkingPlace(Long id) throws WorkstocksBusinessException {
		Optional<WorkingPlace> wp = workingPlaceRepository.findById(id);
		if (wp.isPresent()) {
			WorkingPlace workingPlace = wp.get();
			if (!(AuthUtility.getCurrentCompanyOwner().getCompany().getId().equals(workingPlace.getCompany().getId()))) {
				throw new AccessDeniedException("user unauthorized");
			}

			return workingPlace;
		} else {
			throw new WorkstocksBusinessException("Working place not found");
		}
	}

	@Override
	public WorkingPlaceDto findWorkingPlaceById(Long id) throws WorkstocksBusinessException {
		return mapper.toDto(getOptionalWorkingPlace(id));
	}

	@Override
	@Transactional(rollbackFor = WorkstocksBusinessException.class)
	public void deleteWorkingPlaceById(Long id) throws WorkstocksBusinessException {
		workingPlaceRepository.delete(getOptionalWorkingPlace(id));
	}


	@Override
	public PaginatedDtoResponse<CompanyDto> searchCompanies(PaginatedRequest request) {
		PaginatedDtoResponse<CompanyDto> dtoReponse = new PaginatedDtoResponse<>();
	
		PaginatedEntityResponse<Company> daoPaginatedResult = companyDao.searchCompany(request);

	
		dtoReponse.setElements(mapper.toDtoSet(daoPaginatedResult.getElements()));
		dtoReponse.setResponse(daoPaginatedResult.getReponse());		
		

		return dtoReponse;
	}
	
	@Override
	public long countAllCompany() throws WorkstocksBusinessException  {
		return companyRepository.count();
	}
	
	@Override
	public Set<CompanyDto> findMostRatedCompanies() throws WorkstocksBusinessException {
		Page<Company> companyPage = reviewRepository.getPopularCompanies(PageRequest.of(0, 5));
		return mapper.toDtoSet(new LinkedHashSet<>(companyPage.getContent()));		
	}

	@Override
	public boolean isModifyMainWorkingPlace(WorkingPlaceDto workingPlaceDto) throws WorkstocksBusinessException {
		if (workingPlaceDto.getId() != null) {
			WorkingPlace wp = getOptionalWorkingPlace(workingPlaceDto.getId());
			if (wp.isMainWorkingPlace() && !workingPlaceDto.isMainWorkingPlace()) {
				return true;
			}
		}
		
		return false;
	}
	
	

}
