package it.workstocks.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import it.workstocks.dao.ICompanyDao;
import it.workstocks.dto.AggregatorDto;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.dto.search.FiltersDto;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.company.Company;
import it.workstocks.utils.StringUtils;

@Repository
public class CompanyDao implements ICompanyDao {

	@Autowired
	private EntityManager em;

	@Override
	public PaginatedEntityResponse<Company> searchCompany(PaginatedRequest request) {
		PaginatedEntityResponse<Company> response = new PaginatedEntityResponse<>();

		Pageable pageable = PageRequest.of(request.getPageNumber() - 1, PaginatedRequest.PAGE_SIZE);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Company> query = builder.createQuery(Company.class);
		Root<Company> root = query.from(Company.class);

		// Preaparo le condizioni per la ricerca
		query.select(root).where(builder.and(preparePredicates(request, builder, root).toArray(new Predicate[0]))).distinct(true);

		// Definisco lo statement della query di ricerca e imposto i parametri di
		// paginazione richiesti
		List<Company> result = em.createQuery(query).setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();

		// Preparo la query sul count totale degli elementi
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<Company> countRoot = countQuery.from(Company.class);
		countQuery.select(builder.countDistinct(countRoot))
				.where(builder.and(preparePredicates(request, builder, countRoot).toArray(new Predicate[0])));

		// Preparo ed eseguo lo statement di count degli elemtni
		Long count = em.createQuery(countQuery).getSingleResult();

		// Preparo l'oggetto di paginazione in output
		Page<Company> page = new PageImpl<>(result, pageable, count);

		PaginatedResponse paginatedReponse = new PaginatedResponse();
		response.setReponse(paginatedReponse);
		paginatedReponse.setTotalElements(page.getTotalElements());
		paginatedReponse.setPageNumber(request.getPageNumber());
		paginatedReponse.setTotalPages(page.getTotalPages());
		response.setElements(new HashSet<>(page.getContent()));

		return response;
	}

	private List<Predicate> preparePredicates(PaginatedRequest request, CriteriaBuilder builder, Root<Company> root) {

		List<Predicate> predicates = new ArrayList<>();

		FiltersDto filters = request.getFilters();

		if (filters != null) {
			if (StringUtils.isNotBlank(filters.getName())) {
				predicates.add(builder.like(root.get("name"), "%" + filters.getName() + "%"));
			}
			if (StringUtils.isNotBlank(request.getFilters().getAddress())) {
				Path<Object> baseAddressPath = root.join("workingPlaces").get("address");
				// Costruisco un path che concatena i campi degl'indirizzo
				Expression<String> expression = builder.concat(
						builder.concat(baseAddressPath.get("street"), baseAddressPath.get("city")),
						baseAddressPath.get("country"));
				// Faccio una like sull'indirizzo
				predicates.add(builder.like(expression, "%" + request.getFilters().getAddress() + "%"));
			}

			if (filters.getEmployeesNumber() != null && !filters.getEmployeesNumber().isEmpty()) {
				Path<Integer> employeesNumberPath = root.get("employeesNumber");
				for (AggregatorDto<Integer> f : filters.getEmployeesNumber()) {
					prepareFromAggregator(builder, predicates, employeesNumberPath, f);
				}
			}
			if (filters.getFoundationYear() != null && !filters.getFoundationYear().isEmpty()) {
				Path<Integer> foundationYearPath = root.get("foundationYear");
				for (AggregatorDto<Integer> f : filters.getFoundationYear()) {
					prepareFromAggregator(builder, predicates, foundationYearPath, f);
				}
			}

		}

		return predicates;
	}

	private void prepareFromAggregator(CriteriaBuilder builder, List<Predicate> predicates, Path<Integer> employeesNumberPath,
			AggregatorDto<Integer> f) {
		switch (f.getOperator()) {
		case GREATER_EQUAL:
			predicates.add(builder.greaterThanOrEqualTo(employeesNumberPath, f.getValue()));
			break;
		case LESS_EQUAL:
			predicates.add(builder.lessThanOrEqualTo(employeesNumberPath, f.getValue()));
			break;
		case LESS:
			predicates.add(builder.lessThan(employeesNumberPath, f.getValue()));
			break;
		default:
			break;
		}
	}
}
