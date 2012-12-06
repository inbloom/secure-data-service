/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.api.security.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.PathSegment;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.validator.IContextValidator;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.container.ContainerRequest;

/**
 * ContextValidator
 * Determines if the principal has context to a resource.
 * Verifies the requested endpoint is accessible by the principal
 */
@Component
public class ContextValidator implements ApplicationContextAware {

	private List<IContextValidator> validators;

	@Autowired
	private ResourceHelper resourceHelper;

	@Autowired
	private PagingRepositoryDelegate<Entity> repo;
	
    @Autowired
    private EntityDefinitionStore store;

	private Map<String, String> ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION;

	@PostConstruct
	private void init() {
		ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION = new HashMap<String, String>() {{
			put(EntityNames.ATTENDANCE, "");
			put(EntityNames.COHORT, "");
			put(EntityNames.COURSE, "");
			put(EntityNames.COURSE_OFFERING, "");
			put(EntityNames.COURSE_TRANSCRIPT, "");
			put(EntityNames.DISCIPLINE_INCIDENT, "");
			put(EntityNames.DISCIPLINE_ACTION, "");
			put(EntityNames.GRADEBOOK_ENTRY, "");
			put(EntityNames.GRADUATION_PLAN, "");
			put(EntityNames.PROGRAM, "");
			put(EntityNames.SECTION, "");
			put(EntityNames.SESSION, "");
			put(EntityNames.STUDENT_COHORT_ASSOCIATION, "");
			put(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, "");
			put(EntityNames.STUDENT_PROGRAM_ASSOCIATION, "");
			put(EntityNames.STUDENT_GRADEBOOK_ENTRY, "");
			put(EntityNames.STUDENT_SCHOOL_ASSOCIATION, "");
			put(EntityNames.STUDENT_SECTION_ASSOCIATION, "");
		}};
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		validators = new ArrayList<IContextValidator>();
		validators.addAll(applicationContext.getBeansOfType(IContextValidator.class).values());
	}

	public void validateContextToUri(ContainerRequest request, SLIPrincipal principal) {
		validateUserHasContextToRequestedEntities(request, principal);
		boolean isValid = true;
		
		if (request.getMethod() != "GET") {
			if(request.getMethod() != "POST") {
				// look if we have ed org write context to already existing entity
				int RESOURCE_SEGMENT_INDEX = 1;
				int IDS_SEGMENT_INDEX = 2;
				if (request.getPathSegments().size() > IDS_SEGMENT_INDEX) {
					String resourceName = request.getPathSegments().get(RESOURCE_SEGMENT_INDEX).getPath();
					String id = request.getPathSegments().get(IDS_SEGMENT_INDEX).getPath();
					Entity entity = repo.findById(store.lookupByResourceName(resourceName).getStoredCollectionName(), id);
					isValid = isValidForEdOrgWrite(entity, principal);
				}
				// check write context of update
			}
			Entity entity = request.getEntity(Entity.class);
			if (ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION.get(entity.getType()) != null) {
				String edOrgId = (String) entity.getBody().get(ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION.get(entity.getType()));
				isValid = principal.getSubEdOrgHierarchy().contains(edOrgId);
			}
		}
		
		if(!isValid) {
			throw new AccessDeniedException("Trying to write an entity outside of your education organization hierarchy");
		}
	}
	

	private boolean isValidForEdOrgWrite(Entity entity , SLIPrincipal principal) {
		boolean isValid = true;

			if (ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION.get(entity.getType()) != null) {
				Collection<String> principalsEdOrgs = principal.getSubEdOrgHierarchy(); //TODO initialize the principal hierarchy
				String edOrgId = (String) entity.getBody().get(ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION.get(entity.getType()));
				isValid = principalsEdOrgs.contains(edOrgId);
			}
		return isValid;
	}

	private String getEntityEdOrg(ContainerRequest request, EntityDefinition entityDef) {
		int IDS_SEGMENT_INDEX = 2;
		String edOrgId = "";
		List<PathSegment> pathSegs = request.getPathSegments();
		
		if (pathSegs.size() > IDS_SEGMENT_INDEX) {
			String id = pathSegs.get(IDS_SEGMENT_INDEX).getPath();
			Entity entity = repo.findById(entityDef.getStoredCollectionName(), id);
			edOrgId = (String) entity.getBody().get(ENTITIES_NEEDING_ED_ORG_WRITE_VALIDATION.get(entityDef.getType()));
		}
		return edOrgId;  
	}

	private EntityDefinition getEntityDefinition(ContainerRequest request) {
		List<PathSegment> pathSegments = request.getPathSegments();
		String resource = pathSegments.get(1).getPath();
		EntityDefinition entityDef = store.lookupByResourceName(resource);
		return entityDef;
	}

	private void validateUserHasContextToRequestedEntities(ContainerRequest request, SLIPrincipal principal) {

		List<PathSegment> segs = request.getPathSegments();
		for (Iterator<PathSegment> i = segs.iterator(); i.hasNext(); ) {
			if (i.next().getPath().isEmpty()) {
				i.remove();
			}
		}

		if (segs.size() < 3) {
			return;
		}

		String rootEntity = segs.get(1).getPath();
		EntityDefinition def = resourceHelper.getEntityDefinition(rootEntity);
		if (def == null) {
			return;
		}

		/*
		 * e.g.
		 * !isTransitive - /v1/staff/<ID>/disciplineActions
		 * isTransitive - /v1/staff/<ID>
		 */
		boolean isTransitive = segs.size() < 4;

		/**
		 * If we are v1/entity/id and the entity is "public" don't validate
		 *
		 * Unless of course you're posting/putting/deleting, blah blah blah.
		 */
		if (segs.size() == 3 || (segs.size() == 4 && segs.get(3).getPath().equals("custom"))) {
			if (def.getStoredCollectionName().equals(EntityNames.EDUCATION_ORGANIZATION)) {
				if (!request.getMethod().equals("GET")) {
					isTransitive = false;
				} else {
					info("Not validating access to public entity and it's custom data");
					return;
				}

			}
		}


		String idsString = segs.get(2).getPath();
		Set<String> ids = new HashSet<String>(Arrays.asList(idsString.split(",")));
		validateContextToEntities(def, ids, isTransitive);
	}

	public void validateContextToEntities(EntityDefinition def, Collection<String> ids, boolean isTransitive) {

		IContextValidator validator = findValidator(def.getType(), isTransitive);
		if (validator != null) {
			Set<String> idsToValidate = new HashSet<String>();
			NeutralQuery getIdsQuery = new NeutralQuery(new NeutralCriteria("_id", "in", new ArrayList<String>(ids)));
			int found = 0;
			for (Entity ent : repo.findAll(def.getStoredCollectionName(), getIdsQuery)) {
				found++;
				if (SecurityUtil.principalId().equals(ent.getMetaData().get("createdBy"))
						&& "true".equals(ent.getMetaData().get("isOrphaned"))) {
					debug("Entity is orphaned: id {} of type {}", ent.getEntityId(), ent.getType());
				} else {
					idsToValidate.add(ent.getEntityId());
				}
			}

			if (found != ids.size()) {
				debug("Invalid reference, an entity does not exist. collection: {} ids: {}",
						def.getStoredCollectionName(), ids);
				throw new EntityNotFoundException("Could not locate " + def.getType() + " with ids " + ids);
			}

			if (!idsToValidate.isEmpty()) {
				if (!validator.validate(def.getType(), idsToValidate)) {
					throw new AccessDeniedException("Cannot access entities " + ids);
				}
			}
		} else {
			throw new AccessDeniedException("No validator for " + def.getType() + ", transitive=" + isTransitive);
		}
	}

	/**
	 * @param toType
	 * @param isTransitive
	 * @return
	 * @throws IllegalStateException
	 */
	private IContextValidator findValidator(String toType, boolean isTransitive) throws IllegalStateException {

		IContextValidator found = null;
		for (IContextValidator validator : this.validators) {
			if (validator.canValidate(toType, isTransitive)) {
				info("Using {} to validate {}", new Object[]{validator.getClass().toString(), toType});
				found = validator;
				break;
			}
		}

		if (found == null) {
			warn("No {} validator to {}.", isTransitive ? "TRANSITIVE" : "NOT TRANSITIVE", toType);
		}

		return found;
	}

}
