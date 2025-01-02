package org.codesync.service;

import java.util.List;

import org.codesync.domain.SkillVO;
import org.codesync.mapper.SkillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkillServiceImpl implements SkillService{
	@Autowired
	private SkillMapper mapper;
	
	@Override
	public List<SkillVO> getSkillList(int projectNo) {
		return mapper.getSkillList(projectNo);
	}

	@Override
    public void insertDefaultSkills(List<SkillVO> skills) {
        for (SkillVO skill : skills) {
        	mapper.insertSkill(skill);
        }
    }
	
	@Override
    public void updateCategory(SkillVO skill) {
		mapper.updateCategory(skill);
    }
	
	@Override
	public void addSkill(SkillVO skill) {
		mapper.insertSkill(skill);
	}
	
	@Override
	public void deleteSkill(SkillVO skill) {
		mapper.deleteSkill(skill);
	}
}
