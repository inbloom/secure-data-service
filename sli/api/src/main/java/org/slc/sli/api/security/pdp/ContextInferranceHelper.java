package org.slc.sli.api.security.pdp;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.domain.Entity;
import org.springframework.stereotype.Component;

@Component
public class ContextInferranceHelper {

	// @Resource(name = "validationRepo")
	// private Repository<Entity> repo;

	@Resource
	private EdOrgHelper edorger;

	private Map<String, String> teacherRewrites;
	private Map<String, String> staffRewrites;

	@PostConstruct
	public void init() {
		teacherRewrites.put("sections", "/sections/%s");
		staffRewrites.put("/sections", "/schools/%s/sections");
	}

	public String getInferredUri(String entity, Entity user) {
		String actorId = user.getEntityId();

		String result = "/" + entity;
		// result = "/staff/" + actorId + "/staffCohortAssociations/cohorts";

		if (user.getType().equals("staff")) {
			String ids = StringUtils.join(edorger.getDirectEdOrgAssociations(user), ",");
			String rewrite = staffRewrites.get(entity);

			if (rewrite != null) {
				result = String.format(rewrite, ids);
			}
		}

		return result;
	}
}
