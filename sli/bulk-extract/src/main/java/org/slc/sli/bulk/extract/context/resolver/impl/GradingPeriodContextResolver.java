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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Context resolver for grading period, base on ed org association
 * Basically a copy and paste of cohort resolver and session, but with yet another reference
 * property
 * Ed-fi: Reason #342 why I drink...
 * 
 * @author nbrown
 * 
 */
@Component
public class GradingPeriodContextResolver extends RelatedContextResolver {
    private static final Logger LOG = LoggerFactory.getLogger(GradingPeriodContextResolver.class);
    
   @Autowired
    private EducationOrganizationContextResolver edOrgResolver;
    
    @Override
    protected String getReferredId(String type, Map<String, Object> body) {
        LOG.debug("getting referredId for {}", body);
        @SuppressWarnings("unchecked")
        Map<String, Object> identity = (Map<String, Object>) body.get("gradingPeriodIdentity");
        if (identity == null) {
            return null;
        }
        return super.getReferredId(type, identity);
    }

    @Override
    protected String getReferenceProperty(String entityType) {
        return "schoolId";
    }
    
    @Override
    protected ReferrableResolver getReferredResolver() {
        return edOrgResolver;
    }
    
}
