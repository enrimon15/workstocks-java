package it.workstocks.service;

import java.util.Set;

import it.workstocks.entity.enums.SkillType;
import it.workstocks.exception.WorkstocksBusinessException;

public interface SkillService {

	public Set<String> getPopularSkills(SkillType type) throws WorkstocksBusinessException;

}
