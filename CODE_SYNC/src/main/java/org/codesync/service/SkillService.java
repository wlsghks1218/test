package org.codesync.service;

import java.util.List;

import org.codesync.domain.SkillVO;

public interface SkillService {
	public List<SkillVO> getSkillList(int projectNo);
	public void insertDefaultSkills(List<SkillVO> skills);
    public void updateCategory(SkillVO skill);
    public void addSkill(SkillVO skill);
    public void deleteSkill(SkillVO skill);
    
}
