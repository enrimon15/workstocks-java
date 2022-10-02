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

import it.workstocks.dao.IApplicantDao;
import it.workstocks.dto.AggregatorDto;
import it.workstocks.dto.pagination.PaginatedResponse;
import it.workstocks.dto.search.FiltersDto;
import it.workstocks.dto.search.PaginatedRequest;
import it.workstocks.entity.PaginatedEntityResponse;
import it.workstocks.entity.enums.SkillType;
import it.workstocks.entity.user.applicant.Applicant;
import it.workstocks.utils.StringUtils;

@Repository
public class ApplicantDao implements IApplicantDao {

	@Autowired
	private EntityManager em;

	@Override
	public PaginatedEntityResponse<Applicant> searchApplicant(PaginatedRequest request) {
		PaginatedEntityResponse<Applicant> response = new PaginatedEntityResponse<>();

		Pageable pageable = PageRequest.of(request.getPageNumber() - 1, PaginatedRequest.PAGE_SIZE);
		CriteriaBuilder builder = em.getCriteriaBuilder();

		CriteriaQuery<Applicant> query = builder.createQuery(Applicant.class);
		Root<Applicant> root = query.from(Applicant.class);

		// Preaparo le condizioni per la ricerca
		query.select(root).where(builder.and(preparePredicates(request, builder, root).toArray(new Predicate[0]))).distinct(true);

		// Definisco lo statement della query di ricerca e imposto i parametri di
		// paginazione richiesti
		List<Applicant> result = em.createQuery(query).setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();

		// Preparo la query sul count totale degli elementi
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<Applicant> countRoot = countQuery.from(Applicant.class);
		countQuery.select(builder.countDistinct(countRoot))
				.where(builder.and(preparePredicates(request, builder, countRoot).toArray(new Predicate[0])));

		// Preparo ed eseguo lo statement di count degli elemtni
		Long count = em.createQuery(countQuery).getSingleResult();

		// Preparo l'oggetto di paginazione in output
		Page<Applicant> page = new PageImpl<>(result, pageable, count);

		PaginatedResponse paginatedReponse = new PaginatedResponse();
		response.setReponse(paginatedReponse);
		paginatedReponse.setTotalElements(page.getTotalElements());
		paginatedReponse.setPageNumber(request.getPageNumber());
		paginatedReponse.setTotalPages(page.getTotalPages());
		response.setElements(new HashSet<>(page.getContent()));

		return response;
	}

	private List<Predicate> preparePredicates(PaginatedRequest request, CriteriaBuilder builder, Root<Applicant> root) {

		List<Predicate> predicates = new ArrayList<>();

		FiltersDto filters = request.getFilters();

		if (filters != null) {
			if (StringUtils.isNotBlank(filters.getJobTitle())) {
				predicates.add(builder.like(root.get("jobTitle"), "%" + filters.getJobTitle() + "%"));
			}
			if (StringUtils.isNotBlank(request.getFilters().getAddress())) {
				// Costruisco un path che concatena i campi degl'indirizzo
				Path<String> street = root.get("address").get("street"); 
				Path<String> city = root.get("address").get("city");
				Path<String> country = root.get("address").get("country");
				
				Predicate streetPredicate = builder.like(street, "%" + request.getFilters().getAddress() + "%");
				Predicate cityPredicate = builder.like(city, "%" + request.getFilters().getAddress() + "%");
				Predicate countryPredicate = builder.like(country, "%" + request.getFilters().getAddress() + "%");
				
				// Faccio una like sull'indirizzo
				predicates.add(builder.or(streetPredicate,cityPredicate,countryPredicate));
			}

			if (StringUtils.isNotBlank(request.getFilters().getNameOrSurname())) {
				// Costruisco un path che concatena i campi nome e cognome
				Expression<String> expressionNameSurname = builder.concat(root.get("name"), root.get("surname"));
				Expression<String> expressionSurnameName = builder.concat(root.get("surname"), root.get("name"));
				// Faccio una like sul nome e cognome
				predicates.add(builder.or(
						builder.like(expressionNameSurname, "%" + request.getFilters().getNameOrSurname() + "%"),
						builder.like(expressionSurnameName, "%" + request.getFilters().getNameOrSurname() + "%")));
			}

			if (filters.getSalary() != null && !filters.getSalary().isEmpty()) {
				for (AggregatorDto<Integer> f : filters.getSalary()) {
					prepareSalaryAggregator(builder, predicates, root.get("minimumExpectedSalary"), f);
				}
			}
			
			if (StringUtils.isNotBlank(filters.getSkill())) {
				predicates.add(builder.and(
						builder.like(root.join("skills").get("name"), "%" + filters.getSkill() + "%"),
						builder.equal(root.join("skills").get("skillType"), SkillType.APPLICANT)));
			}

		}

		return predicates;
	}

	private void prepareSalaryAggregator(CriteriaBuilder builder, List<Predicate> predicates, Path<Integer> path,
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
		default:
			break;
		}
	}

}
