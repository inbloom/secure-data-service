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
import org.slc.sli.bulk.extract.lea.EntityToEdOrgDateCache;
import org.slc.sli.bulk.extract.lea.ExtractorHelper;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.ParameterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;

import java.util.Arrays;
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
    public final static String STAFF_REFERENCE = "staffReference";
    public final static String EDORG_REFERENCE = "educationOrganizationReference";
    public final static String END_DATE = "endDate";

    private static final Logger LOG = LoggerFactory.getLogger(SectionContextResolver.class);

    @Autowired
    private EducationOrganizationContextResolver edOrgResolver;
    
    @Autowired
    private DateHelper dateHelper;
    
    private EdOrgHierarchyHelper edOrgHelper;

    @Autowired
    private EdOrgExtractHelper edOrgExtractHelper;

    private EdOrgHierarchyHelper edOrgHierarchyHelper;

    private ExtractorHelper extractorHelper;

    private Set<String> nonDatedEntities = new HashSet<String>(Arrays.asList(EntityNames.STAFF, EntityNames.TEACHER));

    private EntityToEdOrgDateCache staffDatedCache = new EntityToEdOrgDateCache();

    @PostConstruct
    public void init() {
    	edOrgHelper = new EdOrgHierarchyHelper(getRepo());
        extractorHelper = new ExtractorHelper(edOrgExtractHelper);
        edOrgHierarchyHelper = new EdOrgHierarchyHelper(getRepo());
    }
    
    @Override
    protected Set<String> resolve(Entity entity) {
        Set<String> leas = new HashSet<String>();

        String id = entity.getEntityId();
        if (id == null) {
            return leas;
        }
        
        Iterable<Entity> staffEdorgAssociations = getRepo().findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, buildStaffEdorgQuery(id));
        for (Entity association : staffEdorgAssociations) {
            if (!dateHelper.isFieldExpired(association.getBody(), END_DATE)) {
                String edorgReference = (String) association.getBody().get(EDORG_REFERENCE);
               
                if (edorgReference != null) {
                	Entity edOrg = getRepo().findById(EntityNames.EDUCATION_ORGANIZATION, edorgReference);
                    if(edOrg!=null && edOrgHelper.isSEA(edOrg))
                    {
                    	continue;
                    }
                    else {
                    	leas.addAll(edOrgResolver.findGoverningEdOrgs(edorgReference));
                    }
                }
            }
        }
        
        return leas;
    }

    @Override
    protected Set<String> resolve(Entity staff, Entity entityToExtract) {
        Set<String> edOrgs = new HashSet<String>();

        String staffId = staff.getEntityId();

        Iterable<Entity> staffEdorgAssociations = getRepo().findAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, buildStaffEdorgQuery(staffId));

        for (Entity seoa : staffEdorgAssociations) {

            Map<String, DateTime> edOrgDates = new HashMap<String, DateTime>();

            extractorHelper.updateEdorgToDateMap(seoa.getBody(), edOrgDates,
                    ParameterConstants.EDUCATION_ORGANIZATION_REFERENCE, ParameterConstants.BEGIN_DATE, ParameterConstants.END_DATE);

            for (Map.Entry<String, DateTime> edorgDate : edOrgDates.entrySet()) {
                try {
                    if (nonDatedEntities.contains(entityToExtract.getType())
                            || shouldExtract(entityToExtract, edorgDate.getValue())) {

                        edOrgs.add(edorgDate.getKey());
                    }
                } catch (RuntimeException e) {
                    LOG.warn("Could not parse school " + edorgDate.getKey(), e);
                }
            }
        }

        return removeSEA(edOrgs);
    }

    private Set<String> removeSEA(Set<String> edOrgs) {
        edOrgs.remove(edOrgHierarchyHelper.getSEAId());
        return edOrgs;
    }

    NeutralQuery buildStaffEdorgQuery(String id) {
        NeutralQuery q = new NeutralQuery(new NeutralCriteria(STAFF_REFERENCE, NeutralCriteria.OPERATOR_EQUAL, id));
        return q;
    }

    protected boolean shouldExtract(Entity entity, DateTime dateTime) {
        return EntityDateHelper.shouldExtract(entity, dateTime);
    }

    void setDateHelper(DateHelper dateHelper) {
        this.dateHelper = dateHelper;
    }

    @Override
    protected String getCollection() {
        return EntityNames.STAFF;
    }

}
