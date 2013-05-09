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
import java.util.Map;
import java.util.Set;

import com.google.common.collect.MapMaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.context.resolver.ContextResolver;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Context resolver for students
 * 
 * @author nbrown
 * 
 */
@Component
public class StudentContextResolver implements ContextResolver {
    private static final Logger LOG = LoggerFactory.getLogger(StudentContextResolver.class);
    
    public static final String EXIT_WITHDRAW_DATE = "exitWithdrawDate";

    @Autowired
    @Qualifier("secondaryRepo")
    private Repository<Entity> repo;
    private final Map<String, Set<String>> studentEdOrgCache = new MapMaker().softValues().makeMap();
    @Autowired
    private EducationOrganizationContextResolver edOrgResolver;
    
    @Autowired
    private DateHelper dateHelper;
    

    @Override
    public Set<String> findGoverningLEA(Entity entity) {
        Set<String> leas = new HashSet<String>();
        if (getStudentEdOrgCache().containsKey(entity.getEntityId())) {
            LOG.debug("got LEAs from cache for {}", entity);
            return getStudentEdOrgCache().get(entity.getEntityId());
        }

        List<Map<String, Object>> schools = entity.getDenormalizedData().get("schools");
        if (schools != null) {
            for (Map<String, Object> school : schools) {
                try {
                    if (!dateHelper.isFieldExpired(school, EXIT_WITHDRAW_DATE)) {
                        String schoolId = (String) school.get("_id");
                        Set<String> edOrgs = edOrgResolver.findGoverningLEA(schoolId);
                        if (edOrgs != null) {
                            leas.addAll(edOrgs);
                        }
                    }
                } catch (RuntimeException e) {
                    LOG.warn("Could not parse school " + school, e);
                }
            }
        }
        getStudentEdOrgCache().put(entity.getEntityId(), leas);
        return leas;
    }
    
    /**
     * Find the LEAs based on a given student id
     * Will use local cache if available, otherwise will pull student from Mongo
     * 
     * @param id
     *            the ID of the student
     * @return the set of LEAs
     */
    public Set<String> getLEAsForStudentId(String id) {
        Set<String> cached = getStudentEdOrgCache().get(id);
        if (cached != null) {
            return cached;
        }
        Entity entity = repo.findOne("student", new NeutralQuery(new NeutralCriteria("_id",
                NeutralCriteria.OPERATOR_EQUAL, id)).setEmbeddedFieldString("schools"));
        if (entity != null) {
            return findGoverningLEA(entity);
        }
        return new HashSet<String>();
    }
    
    Repository<Entity> getRepo() {
        return repo;
    }
    
    void setRepo(Repository<Entity> repo) {
        this.repo = repo;
    }
    
    Map<String, Set<String>> getStudentEdOrgCache() {
        return studentEdOrgCache;
    }
    
    void setDateHelper(DateHelper dateHelper) {
        this.dateHelper = dateHelper;
    }
}
