package it.workstocks.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.workstocks.entity.enums.SkillType;
import it.workstocks.entity.user.applicant.curricula.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>{
	Set<Skill> findAllByApplicantIdAndSkillType(Long applicantId, SkillType skillType);
}
