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
public class StudentContextResolver extends ReferrableResolver {
    private static final Logger LOG = LoggerFactory.getLogger(StudentContextResolver.class);
    
    public static final String EXIT_WITHDRAW_DATE = "exitWithdrawDate";

    private final Map<String, Set<String>> studentEdOrgCache = new MapMaker().softValues().makeMap();
    @Autowired
    private EducationOrganizationContextResolver edOrgResolver;
    
    @Autowired
    private DateHelper dateHelper;
    

    @Override
    public Set<String> findGoverningLEA(Entity entity) {
        Set<String> leas = new HashSet<String>();
        if (getCache().containsKey(entity.getEntityId())) {
            LOG.debug("got LEAs from cache for {}", entity);
            return getCache().get(entity.getEntityId());
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
        getCache().put(entity.getEntityId(), leas);
        return leas;
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
}
