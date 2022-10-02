package it.workstocks.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.company.WorkingPlace;

@Repository
public interface WorkingPlaceRepository extends JpaRepository<WorkingPlace, Long>{
	Set<WorkingPlace> findByCompanyId(Long companyId);
	Set<WorkingPlace> findByCompanyIdAndMainWorkingPlace(Long companyId, boolean isMain);
}
