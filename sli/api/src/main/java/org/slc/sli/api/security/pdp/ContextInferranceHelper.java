package org.slc.sli.api.security.pdp;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.SectionHelper;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ContextInferranceHelper {

	// @Resource(name = "validationRepo")
	// private Repository<Entity> repo;

	@Resource
	private EdOrgHelper edorger;

    @Resource
    private SectionHelper sectionHelper;


    public String getInferredUri(String resource, Entity user) {

        String result = "/" + resource;

        String actorId = user.getEntityId();
        if (isTeacher(user)) {
            if(ResourceNames.ATTENDANCES.equals(resource)) {
                result = String.format("/sections/%s/students/attendances",
                        StringUtils.join(sectionHelper.getTeachersSections(user)));
            }
		} else if(isStaff(user)) {
			if ("cohorts".equals(resource)) {
				result = String.format("/staff/%s/staffCohortAssociations/cohorts", actorId);
			} else if ("sections".equals(resource)) {
				String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
				result = String.format("/schools/%s/sections", ids);
			}
        }

		return result;
	}

    private boolean isTeacher(Entity principal) {
        return principal.getType().equals(EntityNames.TEACHER);
    }
    private boolean isStaff(Entity principal) {
        return principal.getType().equals(EntityNames.STAFF);
    }
}
