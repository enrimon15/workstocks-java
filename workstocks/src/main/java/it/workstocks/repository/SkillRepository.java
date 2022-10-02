package it.workstocks.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.enums.SkillType;
import it.workstocks.entity.pojo.SkillRankPojo;
import it.workstocks.entity.user.applicant.curricula.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>{
	Set<Skill> findAllByApplicantIdAndSkillType(Long applicantId, SkillType skillType);
	 
	@Query(value = "SELECT DISTINCT new it.workstocks.entity.pojo.SkillRankPojo(UPPER(s.name) as name, count(s) as count) "
				 + "FROM  Skill s "
				 + "WHERE s.skillType = ?1 "
				 + "GROUP BY UPPER(s.name) "
				 + "ORDER BY COUNT(s) DESC")
	Page<SkillRankPojo> findPopularSkillByType(SkillType skillType, Pageable pageable);
}
