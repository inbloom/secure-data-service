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

package org.slc.sli.api.criteriaGenerator;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author pghosh
 * Identifies the entity to apply the date filter for the granular data access
 */

@Component
public class EntityIdentifier {

    @Autowired
    private ModelProvider modelProvider;

    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    private String request;


    /**
     *Identifies the entity
     * @param request
     * @return
     */
    public EntityFilterInfo findEntity(String request) {
        this.request = request;
        EntityFilterInfo entityFilterInfo = new EntityFilterInfo();
        List<String> resources = Arrays.asList(request.split("/"));
        String resource = resources.get(resources.size() - 1);
        EntityDefinition definition = entityDefinitionStore.lookupByResourceName(resource);
        if(definition != null) {
            ClassType entityType = modelProvider.getClassType(StringUtils.capitalize(definition.getType()));
            populatePath(entityFilterInfo, entityType, resource);
        }

        //Enable this once all entities have stamped in ComplextTypes.xsd
//        if (entityName.isEmpty() || beginDateAttribute.isEmpty() || endDateAttribute.isEmpty()) {
//            throw new IllegalArgumentException("Cannot Identify execution path for uri " + request);
//        }
        return entityFilterInfo;
    }

    /**
     * finds the filter details
     * @param entityFilterInfo
     * @param entityType
     * @param resource
     */
    private void populatePath(EntityFilterInfo entityFilterInfo, ClassType entityType, String resource) {
        if (populateDateAttributes(entityFilterInfo, entityType) || populateSessionAttribute(entityFilterInfo, entityType)) {
            entityFilterInfo.setEntityName(getEntityName(entityType));
        } else {
            List<String> associations = modelProvider.getAssociatedDatedEntities(entityType);
            boolean entityNotIdentified = true;
            for (String association : associations) {
                if (request.toLowerCase().contains(association.toLowerCase())) {
                    populateConnectionPath(entityFilterInfo, association);
                    entityNotIdentified = false;
                   break;
                }
            }

            if (entityNotIdentified && (associations!=null) && !associations.isEmpty()) {
                populateConnectionPath(entityFilterInfo, associations.get(0));
            }
        }
    }

    /**
     * Finds the path from the requested to date bearing entity recursively
     * @param entityFilterInfo
     * @param associatedEntityName
     */
    private void populateConnectionPath(EntityFilterInfo entityFilterInfo, String associatedEntityName) {
        ClassType entityType = modelProvider.getClassType(associatedEntityName);
        if (populateDateAttributes(entityFilterInfo, entityType) || populateSessionAttribute(entityFilterInfo, entityType)) {
            entityFilterInfo.setEntityName(getEntityName(entityType));
        } else {
            if (entityFilterInfo.getConnectingEntityList() == null) {
                entityFilterInfo.setConnectingEntityList(new ArrayList<String>());
            }
            entityFilterInfo.getConnectingEntityList().add(associatedEntityName);
            List<String> associations = modelProvider.getAssociatedDatedEntities(entityType);
            for (String association: associations) {
                populateConnectionPath(entityFilterInfo, association);
            }
        }
    }

    /**
     * uncapitalize the entity name to match entity type
     * @param entityType
     * @return
     */
    private String getEntityName(ClassType entityType) {
       return StringUtils.uncapitalize(entityType.getName());
    }


    /**
     * validates entity to check if they have sessionId as one of the attributes
     * @param entityFilterInfo
     * @param entityType
     * @return
     */
    private boolean populateSessionAttribute(EntityFilterInfo entityFilterInfo, ClassType entityType) {
        for (AssociationEnd assoc : modelProvider.getAssociationEnds(entityType.getId())) {
            if (assoc.getName().equals("session")) {
                entityFilterInfo.setSessionAttribute(assoc.getAssociatedAttributeName());
            }
        }
        boolean isSessionAttributeFound = (entityFilterInfo.getSessionAttribute() != null ) &&
                (!entityFilterInfo.getSessionAttribute().isEmpty());
        return isSessionAttributeFound;
    }


    /**
     * Checks the model to identify if the entity is time sensitive
     * @param entityFilterInfo
     * @param entityType
     * @return
     */
    private boolean populateDateAttributes(EntityFilterInfo entityFilterInfo, ClassType entityType) {
        final boolean beginDateExists = entityType.getBeginDateAttribute() != null;
        final boolean endDateExists = entityType.getEndDateAttribute() != null;
        final String beginDateAttribute = (beginDateExists ? entityType.getBeginDateAttribute().getName() : modelProvider.getFilterBeginDateOn(entityType));
        final String endDateAttribute = (endDateExists ? entityType.getEndDateAttribute().getName() : modelProvider.getFilterEndDateOn(entityType));

        entityFilterInfo.setBeginDateAttribute(beginDateAttribute);
        entityFilterInfo.setEndDateAttribute(endDateAttribute);

        return ((!beginDateAttribute.isEmpty()) || (!endDateAttribute.isEmpty()));
    }

}
