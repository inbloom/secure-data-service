/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

package org.slc.sli.dal.migration.strategy.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.dal.migration.strategy.MigrationException;
import org.slc.sli.dal.migration.strategy.MigrationStrategy;
import org.slc.sli.domain.Entity;

/**
 * Supports the migration of entities by adding a new field with a default value.
 * 
 * @author ycao 
 */

public class CombineStrategy implements MigrationStrategy {

	public static final String SUBENTITY = "subentity";

	private String subentity;

	@Override
	public Entity migrate(Entity entity) throws MigrationException {

		List<Map<String, Object>> subentityBodies = getSubentityBodies(entity);
		
		if (subentityBodies == null) { 
			return entity;
		}
	
		if (entity.getBody().containsKey(this.subentity)) {
			if (entity.getBody().get(this.subentity) instanceof List<?>) {
				@SuppressWarnings("unchecked")
				List<Object> existingEntities = (List<Object>) entity.getBody().get(this.subentity);
				existingEntities.addAll(subentityBodies);
			} else {
				throw new MigrationException(new RuntimeException("Sub entity field inside body is not a list"));
			}
		} else {
			entity.getBody().put(this.subentity, subentityBodies);
		}
		
		return entity;
	}

	private List<Map<String, Object>> getSubentityBodies(Entity entity) throws MigrationException {
		if (entity.getEmbeddedData() == null) {
			return null;
		}
		
		List<Entity> subentities = entity.getEmbeddedData().remove(this.subentity);
		if (subentities == null || subentities.isEmpty()) {
			return null;
		}

		List<Map<String, Object>> subentityBodies = new ArrayList<Map<String, Object>>();
		for (Entity e : subentities) {
			subentityBodies.add(e.getBody());
	    }

		return subentityBodies;
	}

	@Override
	public void setParameters(Map<String, Object> parameters) throws MigrationException {
		if (parameters == null || parameters.isEmpty()) {
			throw new MigrationException(new IllegalArgumentException("Combine strategy missing required argument: subentity"));
		}
		this.subentity = parameters.get(SUBENTITY).toString();
	}

}
