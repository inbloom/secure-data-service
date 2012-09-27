package org.slc.sli.api.security.pdp;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

@Component
public class ContextInferranceHelper {

	// @Resource(name = "validationRepo")
	// private Repository<Entity> repo;

	@Resource
	private EdOrgHelper edorger;

	public String getInferredUri(String entity, Entity user) {
		String actorId = user.getEntityId();

		String result = "/" + entity;

		if (isTeacher(user)) {
		} else {
			if ("cohorts".equals(entity)) {
				result = String.format("/staff/%s/staffCohortAssociations/cohorts", actorId);
			} else if ("sections".equals(entity)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/sections", ids);
			}
		}

		return result;
	}

	private boolean isTeacher(Entity principal) {
		return principal.getType().equals(EntityNames.TEACHER);
	}
}
