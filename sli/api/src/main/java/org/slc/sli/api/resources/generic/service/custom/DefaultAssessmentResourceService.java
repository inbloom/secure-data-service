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

package org.slc.sli.api.resources.generic.service.custom;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.representation.ServiceResponse;
import org.slc.sli.api.resources.generic.service.ResourceServiceHelper;
import org.slc.sli.api.resources.generic.util.ResourceHelper;
import org.slc.sli.api.selectors.LogicalEntity;
import org.slc.sli.api.selectors.UnsupportedSelectorException;
import org.slc.sli.api.service.query.ApiQuery;
import org.slc.sli.domain.NeutralCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Service class for handling assessment resource requests
 *
 */

@Component
public class DefaultAssessmentResourceService implements AssessmentResourceService {

    @Autowired
    private ResourceHelper resourceHelper;

    @Autowired
    private LogicalEntity logicalEntity;

    @Autowired
    private ResourceServiceHelper resourceServiceHelper;

    @Override
    public ServiceResponse getLearningStandards(final Resource base, final String idList, final Resource returnResource, final URI requestURI) {

        final EntityDefinition baseEntityDefinition = resourceHelper.getEntityDefinition(base);
        final EntityDefinition finalEntityDefinition = resourceHelper.getEntityDefinition(returnResource);
        final List<String> ids = Arrays.asList(idList.split(","));

        ApiQuery apiQuery = resourceServiceHelper.getApiQuery(baseEntityDefinition, requestURI);

        apiQuery.addCriteria(new NeutralCriteria("_id", "in", ids));
        apiQuery.setLimit(0);
        apiQuery.setOffset(0);

        List<EntityBody> baseResults = (List<EntityBody>) baseEntityDefinition.getService().list(apiQuery);

        String key = null;
        if (returnResource.getResourceType().equals("learningStandards")) {
            key = "assessmentItem";
        } else {
            key = "objectiveAssessment";
        }

        List<String> finalIds = new ArrayList<String>();
        for (EntityBody entityBody : baseResults) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) entityBody.get(key);

            if (items != null) {
                for (Map<String, Object> item : items) {
                    finalIds.addAll((List<String>) item.get(returnResource.getResourceType()));
                }
            }
        }

        apiQuery = resourceServiceHelper.getApiQuery(finalEntityDefinition, requestURI);
        apiQuery.addCriteria(new NeutralCriteria("_id", "in", finalIds));

        List<EntityBody> finalResults = null;
        try {
            finalResults = logicalEntity.getEntities(apiQuery, returnResource.getResourceType());
        } catch (UnsupportedSelectorException e) {
            finalResults = (List<EntityBody>) finalEntityDefinition.getService().list(apiQuery);
        }

        return new ServiceResponse(finalResults, finalResults.size());
    }
}
