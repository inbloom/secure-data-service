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
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * resolve a staff or teacher
 * 
 * @author ycao
 * 
 */
@Component
public class StaffTeacherContextResolver extends ReferrableResolver {
    private static final Logger LOG = LoggerFactory.getLogger(SectionContextResolver.class);

    @Autowired
    private EducationOrganizationContextResolver edOrgResolver;
    
    @Autowired
    private DateHelper dateHelper;

    private final static String STAFF_REFERENCE = "staffReference";
    private final static String EDORG_REFERENCE = "educationOrganizationReference";
    private final static String END_DATE = "endDate";

    @Override
    public Set<String> findGoverningLEA(Entity entity) {
        Set<String> leas = new HashSet<String>();
        if (getCache().containsKey(entity.getEntityId())) {
            LOG.debug("got LEAs from cache for {}", entity);
            return getCache().get(entity.getEntityId());
        }
       
        String id = entity.getEntityId();
        if (id == null) {
            return leas;
        }
        
        Iterable<Entity> staffEdorgAssociations = getRepo().findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, buildStaffEdorgQuery(id));
        for (Entity association : staffEdorgAssociations) {
            if (!dateHelper.isFieldExpired(association.getBody(), END_DATE)) {
                String edorgReference = (String) association.getBody().get(EDORG_REFERENCE);
                if (edorgReference != null) {
                    leas.addAll(edOrgResolver.findGoverningLEA(edorgReference));
                }
            }
        }
        
        getCache().put(entity.getEntityId(), leas);
        return leas;
    }
    
    NeutralQuery buildStaffEdorgQuery(String id) {
        NeutralQuery q = new NeutralQuery(new NeutralCriteria(STAFF_REFERENCE, NeutralCriteria.OPERATOR_EQUAL, id));
        return q;
    }

    void setDateHelper(DateHelper dateHelper) {
        this.dateHelper = dateHelper;
    }

    @Override
    protected String getCollection() {
        return EntityNames.STAFF;
    }

}
