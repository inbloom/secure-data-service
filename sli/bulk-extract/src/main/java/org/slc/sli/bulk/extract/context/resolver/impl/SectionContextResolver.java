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
package org.slc.sli.bulk.extract.context.resolver.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

/**
 * Context resolver for sections
 * 
 * @author nbrown
 * 
 */
@Component
public class SectionContextResolver extends EdOrgRelatedReferrableResolver {
    private static final Logger LOG = LoggerFactory.getLogger(SectionContextResolver.class);
    
    private final StudentAssociationWalker walker;
    
    @Autowired
    public SectionContextResolver(StudentAssociationWalker walker) {
        this.walker = walker;
    }

    @Override
    protected Set<String> getTransitiveAssociations(Entity entity) {
        Set<String> leas = new HashSet<String>();
        List<Entity> studentAssociations = entity.getEmbeddedData().get("studentSectionAssociation");
        LOG.debug("studentSectionAssociations being process for {} are {}", entity, studentAssociations);
        leas.addAll(walker.walkStudentAssociations(studentAssociations));
        return leas;
    }

    @Override
    protected String getCollection() {
        return EntityNames.SECTION;
    }
    
}
