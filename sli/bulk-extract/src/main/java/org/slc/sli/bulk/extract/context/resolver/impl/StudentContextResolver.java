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


import com.google.common.collect.MapMaker;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.date.EntityDateHelper;
import org.slc.sli.bulk.extract.lea.EntityToEdOrgDateCache;
import org.slc.sli.bulk.extract.lea.ExtractorHelper;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
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
import org.slc.sli.domain.Entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Context resolver for students
 * 
 * @author nbrown
 * 
 */
@Component
public class StudentContextResolver extends ReferrableResolver implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(StudentContextResolver.class);


    private final Map<String, Set<String>> studentEdOrgCache = new MapMaker().softValues().makeMap();

    private EntityToEdOrgDateCache datedCache = new EntityToEdOrgDateCache();

    @Autowired
    private EdOrgExtractHelper edOrgExtractHelper;

    private EdOrgHierarchyHelper edOrgHierarchyHelper;

    @Autowired
    private EducationOrganizationContextResolver edOrgResolver;

    @Autowired
    private DateHelper dateHelper;

    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;

    private ExtractorHelper extractorHelper;

    private Set<String> nonDatedEntities = new HashSet<String>(Arrays.asList(EntityNames.STUDENT, EntityNames.STUDENT_PARENT_ASSOCIATION, EntityNames.PARENT));

    private String seaIds = null;

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
                    if (!dateHelper.isFieldExpired(school, ParameterConstants.EXIT_WITHDRAW_DATE)) {
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
        Set<String> edorgs = new HashSet<String>();


        String studentId = student.getEntityId();
        Map<String, DateTime> edorgDates = datedCache.getEntriesById(studentId);
        if (edorgDates.isEmpty()) {
            Map<String, DateTime> nonSeaDates = fetchNonSEADates(extractorHelper.fetchAllEdOrgsForStudent(student));

            updateCache(studentId, nonSeaDates);
            edorgDates = datedCache.getEntriesById(studentId);
        }

        for (Map.Entry<String, DateTime> edorgDate : edorgDates.entrySet()) {
            try {
                if (nonDatedEntities.contains(entityToExtract.getType())
                         || shouldExtract(entityToExtract, edorgDate.getValue())) {

                    edorgs.add(edorgDate.getKey());
                }
            } catch (RuntimeException e) {
                LOG.warn("Could not parse school " + edorgDate.getKey(), e);
            }
        }

        return edorgs;
    }

    protected boolean shouldExtract(Entity entity, DateTime dateTime) {
        return EntityDateHelper.shouldExtract(entity, dateTime);
    }

    private void updateCache(String studentId, Map<String, DateTime> edorgDates) {

        for (Map.Entry<String, DateTime> edorgDate: edorgDates.entrySet()) {
            datedCache.addEntry(studentId, edorgDate.getKey(), edorgDate.getValue());
        }
    }

    private Map<String, DateTime> fetchNonSEADates(Map<String, DateTime> edorgDates) {
        if (seaIds == null) {
            seaIds = edOrgHierarchyHelper.getSEAId();
        }

        edorgDates.remove(seaIds);
        return edorgDates;
    }
    
    protected Map<String, Set<String>> getCache() {
        return studentEdOrgCache;
    }

    @Override
    protected String getCollection() {
        return EntityNames.STUDENT;
    }

    public void setExtractorHelper(ExtractorHelper extractorHelper) {
        this.extractorHelper = extractorHelper;
    }

    public void setEdOrgHierarchyHelper(EdOrgHierarchyHelper edOrgHierarchyHelper) {
        this.edOrgHierarchyHelper = edOrgHierarchyHelper;
    }
}
