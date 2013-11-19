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
package org.slc.sli.api.security.context;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Establish ownership of an entity.
 *
 *  @author: npandey ablum
 */

@Component
public abstract class OwnershipArbiter {

    protected Map<String, Reference> typeToReference = new HashMap<String, OwnershipArbiter.Reference>();

    @Autowired
    private EntityDefinitionStore store;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    @Autowired
    private EdOrgHelper helper;

    protected static class Reference {
        // R2L = Right hand side entity contains reference to entity on left hand side
        // L2R = Left hand side entity contains reference to entity on right hand side
        public enum RefType {
            LEFT_TO_RIGHT, RIGHT_TO_LEFT
        };

        String fromType = null;
        String toType = null;
        RefType type = null;
        String refField = null;

        public Reference(String fromType, String toType, String refField, RefType refType) {
            this.fromType = fromType;
            this.toType = toType;
            this.type = refType;
            this.refField = refField;
        }
    }




    public List<Entity> findOwner(Iterable<Entity> entities, String entityType, boolean ignoreOrphans) {
        List<Entity> edorgs = new ArrayList<Entity>();
        debug("checking ownership for entities of type: {}", entityType);

        if (isBaseType(entityType)) {
            // No need to do an actual mongo lookup since we have the IDs we need
            for (Entity entity : entities) {
                edorgs.add(entity);
            }
        } else {
            Reference ref = typeToReference.get(entityType);
            if (ref == null) {
                warn("Cannot handle ownership for entity type {}.", entityType);
                throw new RuntimeException("No ownership for " + entityType);
            }

            for (Entity entity : entities) {
                // Ignore orphaned entities created by the principal.
                if (entity.getMetaData() != null && SecurityUtil.principalId().equals(entity.getMetaData().get("createdBy"))
                        && "true".equals(entity.getMetaData().get("isOrphaned")) && ignoreOrphans) {
                    debug("Entity is orphaned: id {} of type {}", entity.getEntityId(), entity.getType());
                    continue;
                }

                EntityDefinition definition = store.lookupByEntityType(ref.toType);
                String collectionName = definition.getStoredCollectionName();
                
                if(entityType.equals("application")
                        && entity.getBody().get("allowed_for_all_edorgs") != null
                        && (Boolean)entity.getBody().get("allowed_for_all_edorgs")) {
                	Iterable<Entity> ents = repo.findAll(collectionName, new NeutralQuery());
                    for (Entity e : ents) {
                    	edorgs.add(e);
                    }


                } else {                             
                	String critField = null;
                	Object critValue = null;
                	    if (ref.type == Reference.RefType.LEFT_TO_RIGHT) {
                		    critField = ParameterConstants.ID;
                		    critValue = entity.getBody().get(ref.refField);
                	    } else { // RIGHT_TO_LEFT
                		    critField = ref.refField;
                    	    critValue = entity.getEntityId();
                	    }

                	    if(critValue != null) {
                		    Iterable<Entity> ents = repo.findAll(collectionName, new NeutralQuery(new NeutralCriteria(critField,
                				NeutralCriteria.OPERATOR_EQUAL, critValue)));
                		    if (ents.iterator().hasNext()) {
                			    List<Entity> toAdd = findOwner(ents, collectionName, ignoreOrphans);
                			    edorgs.addAll(toAdd);
                		    } else {
                			    // entity does not exist in db so skip
                                throw new APIAccessDeniedException("Could not find a matching " + collectionName + " where "
                                    + critField + " is " + critValue + ".");
                		    }
                	    } else {
                		    // the reference field is uninitialized
                            throw new APIAccessDeniedException("Will not try to find a matching " + collectionName + " because " + entityType + "." + ref.refField + " is null.");
                	    }
                   }
            } //            for (Entity entity : entities)
        }//isBaseType(entityType)

            return edorgs;
      }

    abstract boolean isBaseType(String type);


}
