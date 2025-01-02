package org.codesync.mapper;

import java.util.List;

import org.codesync.domain.SkillVO;

public interface SkillMapper {
	public List<SkillVO> getSkillList(int projectNo);
	public int insertSkill(SkillVO skill);
	public int updateCategory(SkillVO skill);
	public int deleteSkill(SkillVO skill);
}
