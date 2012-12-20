package org.slc.sli.api.security.context.validator;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Validates teacher's access to given teachers
 * 
 * @author dkornishev
 * 
 */
@Component
public class TransitiveTeacherToTeacherSchoolAssociationValidator extends AbstractContextValidator {

	@Resource
	private TransitiveTeacherToTeacherValidator val;

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return EntityNames.TEACHER_SCHOOL_ASSOCIATION.equals(entityType) && isTeacher() && isTransitive;
	}

	@Override
	public boolean validate(String entityType, Set<String> ids) {
		if (!this.canValidate(entityType, true)) {
			throw new IllegalArgumentException(String.format("Asked to validate %s->%s[%s]", SecurityUtil.getSLIPrincipal().getEntity().getType(), entityType, false));
		}

		if (ids == null || ids.size() == 0) {
			throw new IllegalArgumentException("Incoming list of ids cannot be null");
		}

		NeutralQuery nq = new NeutralQuery(new NeutralCriteria("_id", "in", ids));
		Iterable<Entity> tsa = getRepo().findAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, nq);

		Set<String> teachers = new HashSet<String>();
		for (Entity e : tsa) {
			teachers.add((String) e.getBody().get("teacherId"));
		}
		
		if(teachers.isEmpty()) {
			return false;
		}else {
			return val.validate(EntityNames.TEACHER, teachers);
		}
	}
}
