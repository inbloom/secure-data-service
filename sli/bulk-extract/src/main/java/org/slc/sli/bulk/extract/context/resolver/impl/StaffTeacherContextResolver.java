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


import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.lea.ExtractorHelper;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.ParameterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * resolve a staff or teacher
 * 
 * @author ycao
 * 
 */
@Component
public class StaffTeacherContextResolver extends ReferrableResolver {

    @Autowired
    private EdOrgExtractHelper edOrgExtractHelper;

    private EdOrgHierarchyHelper edOrgHierarchyHelper;

    private ExtractorHelper extractorHelper;

    @PostConstruct
    public void init() {
        extractorHelper = new ExtractorHelper(edOrgExtractHelper);
        edOrgHierarchyHelper = new EdOrgHierarchyHelper(getRepo());
    }

    @Override
    protected Set<String> resolve(Entity staff, Entity entityToExtract) {
        Set<String> edOrgs = new HashSet<String>();

        String staffId = staff.getEntityId();

        Iterable<Entity> staffEdorgAssociations = getRepo().findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, buildStaffEdorgQuery(staffId));

        for (Entity seoa : staffEdorgAssociations) {

            Map<String, DateTime> edOrgDates = new HashMap<String, DateTime>();

            edOrgDates = extractorHelper.updateEdorgToDateMap(seoa.getBody(), edOrgDates,
                    ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, ParameterConstants.BEGIN_DATE, ParameterConstants.END_DATE);

            for (Map.Entry<String, DateTime> edorgDate : edOrgDates.entrySet()) {
                if (shouldExtract(entityToExtract, edorgDate.getValue())) {
                    edOrgs.add(edorgDate.getKey());
                }
            }
        }
        return removeSEA(edOrgs);
    }

    private Set<String> removeSEA(Set<String> edOrgs) {
        Entity sea = edOrgHierarchyHelper.getSEA();
        if (sea != null) {
            edOrgs.remove(sea.getEntityId());
        }
        return edOrgs;
    }

    NeutralQuery buildStaffEdorgQuery(String id) {
        NeutralQuery q = new NeutralQuery(new NeutralCriteria(ParameterConstants.STAFF_REFERENCE, NeutralCriteria.OPERATOR_EQUAL, id));
        return q;
    }

    protected boolean shouldExtract(Entity entity, DateTime dateTime) {
        return EntityDateHelper.shouldExtract(entity, dateTime);
    }

    @Override
    protected String getCollection() {
        return EntityNames.STAFF;
    }

    public void setExtractorHelper(ExtractorHelper extractorHelper) {
        this.extractorHelper = extractorHelper;
    }

}
