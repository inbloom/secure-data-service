package org.slc.sli.api.security.context.validator;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.stereotype.Component;

/**
 * Validates teacher's access to given teacherSchoolAssociation
 * This is 'through' validator, and it is invoked if there are
 * additional path-segments.
 * General rule is that you can see things through associations 
 * that have your id on them
 * 
 * @author dkornishev
 * 
 */
@Component
public class TeacherToTeacherSchoolAssociationValidator extends AbstractContextValidator {

	@Resource
	private PagingRepositoryDelegate<Entity> repo;

	@Override
	public boolean canValidate(String entityType, boolean isTransitive) {
		return EntityNames.TEACHER_SCHOOL_ASSOCIATION.equals(entityType) && isTeacher() && !isTransitive;
	}

	@Override
	public boolean validate(String entityType, Set<String> ids) {
		if (!this.canValidate(entityType, false)) {
			throw new IllegalArgumentException(String.format("Asked to validate %s->%s[%s]", SecurityUtil.getSLIPrincipal().getEntity().getType(), entityType, false));
		}

		if (ids == null || ids.size() == 0) {
			throw new IllegalArgumentException("Incoming list of ids cannot be null");
		}

		NeutralQuery nq = new NeutralQuery(new NeutralCriteria("teacherId", "=", SecurityUtil.getSLIPrincipal().getEntity().getEntityId()));
		Iterable<Entity> it = this.repo.findAll(EntityNames.TEACHER_SCHOOL_ASSOCIATION, nq);
		
		Set<String> fin = new HashSet<String>(ids);
		for(Entity e : it) {
			fin.remove(e.getEntityId());
		}
		
		return fin.isEmpty();
	}

}
