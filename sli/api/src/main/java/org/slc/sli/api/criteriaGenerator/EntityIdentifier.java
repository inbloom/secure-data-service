/*
 *
 *  * Copyright 2012 Shared Learning Collaborative, LLC
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.slc.sli.api.criteriaGenerator;

import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.model.ModelProvider;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.TaggedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 1/15/13
 */

@Component
public class EntityIdentifier {

    @Autowired
    ModelProvider modelProvider;

    @Autowired
    private EntityDefinitionStore entityDefinitionStore;

    private String entityName;
    private String beginDateAttribute;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getBeginDateAttribute() {
        return beginDateAttribute;
    }

    public void setBeginDateAttribute(String beginDateAttribute) {
        this.beginDateAttribute = beginDateAttribute;
    }

    public String getEndDateAttribute() {
        return endDateAttribute;
    }

    public void setEndDateAttribute(String endDateAttribute) {
        this.endDateAttribute = endDateAttribute;
    }

    private String endDateAttribute;

    public void findEntity(String request) {
        List<String> resources = Arrays.asList(request.split("/"));
        String resource = resources.get(resources.size() - 1);
        EntityDefinition definition = entityDefinitionStore.lookupByResourceName(resource);
        ClassType entityType = modelProvider.getClassType(StringUtils.capitalize(definition.getType()));
        getAttributes(entityType);
        entityName = resource;
    }

    private void getAttributes(ClassType entityType) {
        beginDateAttribute = entityType.getBeginDateAttribute().getName();
        endDateAttribute = entityType.getEndDateAttribute().getName();
    }
}
