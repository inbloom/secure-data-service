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

import java.util.*;

import com.google.common.collect.MapMaker;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.lea.EntityToEdOrgDateCache;
import org.slc.sli.bulk.extract.lea.ExtractorHelper;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.utils.EdOrgHierarchyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;

/**
 * Context resolver for students
 * 
 * @author nbrown
 * 
 */
@Component
public class StudentContextResolver extends ReferrableResolver implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(StudentContextResolver.class);
    
    public static final String EXIT_WITHDRAW_DATE = "exitWithdrawDate";

    private final Map<String, Set<String>> studentEdOrgCache = new MapMaker().softValues().makeMap();
    @Autowired
    private EducationOrganizationContextResolver edOrgResolver;

    private EntityToEdOrgDateCache datedCache = new EntityToEdOrgDateCache();

    @Autowired
    private DateHelper dateHelper;

    @Autowired
    private EdOrgExtractHelper edOrgExtractHelper;

    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;

    private EdOrgHierarchyHelper edOrgHierarchyHelper;

    private ExtractorHelper extractorHelper;

    private Set<String> nonDatedEntities = new HashSet<String>(Arrays.asList(EntityNames.STUDENT, EntityNames.STUDENT_PARENT_ASSOCIATION));

    @Override
    public void afterPropertiesSet() throws Exception {
        extractorHelper = new ExtractorHelper(edOrgExtractHelper);
        edOrgHierarchyHelper = new EdOrgHierarchyHelper(repo);
    }


    @Override
    public Set<String> resolve (Entity entity) {
        Set<String> leas = new HashSet<String>();

        List<Map<String, Object>> schools = entity.getDenormalizedData().get("schools");
        if (schools != null) {
            for (Map<String, Object> school : schools) {
                try {
                    if (!dateHelper.isFieldExpired(school, EXIT_WITHDRAW_DATE)) {
                        String schoolId = (String) school.get("_id");
                        Set<String> edOrgs = edOrgResolver.findGoverningEdOrgs(schoolId);
                        if (edOrgs != null) {
                            leas.addAll(edOrgs);
                        }
                    }
                } catch (RuntimeException e) {
                    LOG.warn("Could not parse school " + school, e);
                }
            }
        }

        return leas;
    }

    @Override
    public Set<String> resolve(Entity student, Entity entityToExtract) {
        Set<String> leas = new HashSet<String>();


        String studentId = student.getEntityId();
        Map<String, DateTime> edorgDates = datedCache.getEntriesById(studentId);
        if (edorgDates.isEmpty()) {
            edorgDates = extractorHelper.fetchAllEdOrgsForStudent(student);
            updateCache(studentId, edorgDates);
            edorgDates = datedCache.getEntriesById(studentId);
        }

        for (Map.Entry<String, DateTime> edorgDate : edorgDates.entrySet()) {
            try {
                if (nonDatedEntities.contains(entityToExtract.getType())
                         || EntityDateHelper.shouldExtract(entityToExtract, edorgDate.getValue())) {

                    leas.add(edorgDate.getKey());
                }
            } catch (RuntimeException e) {
                LOG.warn("Could not parse school " + edorgDate.getKey(), e);
            }
        }

        return leas;
    }

    private void updateCache(String studentId, Map<String, DateTime> edorgDates) {

        Set<String> allEdorgs = edorgDates.keySet();
        Set<String> nonSeas = fetchNonSEA(allEdorgs);

        for (String edorg : nonSeas) {
            datedCache.addEntry(studentId, edorg, edorgDates.get(edorg));
        }
    }

    private Set<String> fetchNonSEA(Set<String> edorgIds) {
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria(ParameterConstants.ID, NeutralCriteria.CRITERIA_IN, edorgIds));

        Iterable<Entity> edorgs = repo.findAll(EntityNames.EDUCATION_ORGANIZATION, query);

        Set<String> nonSEAs = new HashSet<String>();
        for(Entity edorg : edorgs) {
            if (!edOrgHierarchyHelper.isSEA(edorg)) {
                nonSEAs.add(edorg.getEntityId());
            }
        }
        return nonSEAs;
    }
    
    protected Map<String, Set<String>> getCache() {
        return studentEdOrgCache;
    }
    
    void setDateHelper(DateHelper dateHelper) {
        this.dateHelper = dateHelper;
    }

    @Override
    protected String getCollection() {
        return EntityNames.STUDENT;
    }

    public void setEdOrgResolver(EducationOrganizationContextResolver edOrgResolver) {
        this.edOrgResolver = edOrgResolver;
    }

    public void setExtractorHelper(ExtractorHelper extractorHelper) {
        this.extractorHelper = extractorHelper;
    }
}
