package it.workstocks.service.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.workstocks.entity.enums.SkillType;
import it.workstocks.entity.pojo.SkillRankPojo;
import it.workstocks.repository.SkillRepository;
import it.workstocks.service.SkillService;

@Service
@Transactional(readOnly = true)
public class SkillServiceImpl implements SkillService {

	@Autowired
	private SkillRepository skillRepository;

	@Override
	public Set<String> getPopularSkills(SkillType type) {
		Set<String> skills = new LinkedHashSet<>();
		Pageable pageable = PageRequest.of(0, 5);
		for(SkillRankPojo sr: skillRepository.findPopularSkillByType(type, pageable).getContent()) {
			skills.add(sr.getName());
		}
		return skills;
	}

}
