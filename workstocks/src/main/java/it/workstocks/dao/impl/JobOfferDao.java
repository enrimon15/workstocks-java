package it.workstocks.dao.impl;

import java.time.LocalDate;
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

import it.workstocks.dao.IJobOfferDao;
import it.workstocks.dto.AggregatorDto;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.dto.search.FiltersDto;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.job.JobOffer;
import it.workstocks.utils.StringUtils;

@Repository
public class JobOfferDao implements IJobOfferDao {

	@Autowired
	private EntityManager em;

	@Override
	public PaginatedEntityResponse<JobOffer> searchJobOffer(PaginatedRequest request) {
		PaginatedEntityResponse<JobOffer> response = new PaginatedEntityResponse<>();

		Pageable pageable = PageRequest.of(request.getPageNumber() - 1, PaginatedRequest.PAGE_SIZE);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<JobOffer> query = builder.createQuery(JobOffer.class);
		Root<JobOffer> root = query.from(JobOffer.class);

		// Preaparo le condizioni per la ricerca
		query.select(root)
		.where(builder.and(preparePredicates(request, builder, root).toArray(new Predicate[0]))).distinct(true)
		.orderBy(builder.desc(root.get("createdAt")));

		// Definisco lo statement della query di ricerca e imposto i parametri di
		// paginazione richiesti
		List<JobOffer> result = em.createQuery(query).setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();

		// Preparo la query sul count totale degli elementi
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<JobOffer> countRoot = countQuery.from(JobOffer.class);
		countQuery.select(builder.countDistinct(countRoot))
				.where(builder.and(preparePredicates(request, builder, countRoot).toArray(new Predicate[0])));

		// Preparo ed eseguo lo statement di count degli elemtni
		Long count = em.createQuery(countQuery).getSingleResult();

		// Preparo l'oggetto di paginazione in output
		Page<JobOffer> page = new PageImpl<>(result, pageable, count);

		PaginatedResponse paginatedReponse = new PaginatedResponse();
		response.setReponse(paginatedReponse);
		paginatedReponse.setTotalElements(page.getTotalElements());
		paginatedReponse.setPageNumber(request.getPageNumber());
		paginatedReponse.setTotalPages(page.getTotalPages());
		response.setElements(new HashSet<>(page.getContent()));

		return response;
	}

	private List<Predicate> preparePredicates(PaginatedRequest request, CriteriaBuilder builder, Root<JobOffer> root) {

		List<Predicate> predicates = new ArrayList<>();

		FiltersDto filters = request.getFilters();

		if (filters != null) {
			if (StringUtils.isNotBlank(filters.getJobTitle())) {
				predicates.add(builder.like(root.get("title"), "%" + filters.getJobTitle() + "%"));
			}
			if (StringUtils.isNotBlank(request.getFilters().getAddress())) {
				Path<Object> baseAddressPath = root.join("workingPlace").get("address");
				// Costruisco un path che concatena i campi degl'indirizzo
				Expression<String> expression = builder.concat(
						builder.concat(baseAddressPath.get("street"), baseAddressPath.get("city")),
						baseAddressPath.get("country"));
				// Faccio una like sull'indirizzo
				predicates.add(builder.like(expression, "%" + request.getFilters().getAddress() + "%"));
			}

			if (StringUtils.isNotBlank(request.getFilters().getCompanyName())) {
				predicates.add(builder.like(root.join("workingPlace").join("company").get("name"),
						"%" + request.getFilters().getCompanyName() + "%"));
			}
			
			if (request.getFilters().getOfferType() != null) {
				predicates.add(builder.equal(root.get("contractType"),request.getFilters().getOfferType()));
			}

			if (filters.getExperience() != null && !filters.getExperience().isEmpty()) {
				Path<Integer> foundationYearPath = root.get("experience");
				for (AggregatorDto<Integer> f : filters.getExperience()) {
					prepareGenericAggregator(builder, predicates, foundationYearPath, f);
				}
			}

			if (filters.getSalary() != null && !filters.getSalary().isEmpty()) {
				for (AggregatorDto<Integer> f : filters.getSalary()) {
					prepareSalaryAggregator(builder, predicates, root, f);
				}
			}

		}
		
		predicates.add(builder.greaterThanOrEqualTo(root.get("dueDate"), LocalDate.now()));
		return predicates;
	}

	private void prepareSalaryAggregator(CriteriaBuilder builder, List<Predicate> predicates, Root<JobOffer> root,
			AggregatorDto<Integer> f) {
		Path<Integer> pathMin = root.get("minSalary");
		Path<Integer> pathMax = root.get("maxSalary");

		switch (f.getOperator()) {
		case GREATER_EQUAL:
			predicates.add(builder.greaterThanOrEqualTo(pathMin, f.getValue()));
			break;
		case LESS_EQUAL:
			predicates.add(builder.lessThanOrEqualTo(pathMax, f.getValue()));
			break;
		case LESS:
			predicates.add(builder.lessThan(pathMax, f.getValue()));
			break;
		default:
			break;
		}
	}

	private void prepareGenericAggregator(CriteriaBuilder builder, List<Predicate> predicates, Path<Integer> path,
			AggregatorDto<Integer> f) {
		switch (f.getOperator()) {
		case GREATER_EQUAL:
			predicates.add(builder.greaterThanOrEqualTo(path, f.getValue()));
			break;
		case LESS_EQUAL:
			predicates.add(builder.lessThanOrEqualTo(path, f.getValue()));
			break;
		case LESS:
			predicates.add(builder.lessThan(path, f.getValue()));
			break;
		case EQUAL:
			predicates.add(builder.equal(path, f.getValue()));
			break;
		default:
			break;
		}
	}
}
