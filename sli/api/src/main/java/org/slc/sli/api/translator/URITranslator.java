/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.api.translator;

import com.sun.jersey.spi.container.ContainerRequest;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import javax.ws.rs.core.PathSegment;
import java.util.*;

/**
 * This class aligns request uri with dataModel
 * @author pghosh
 *
 */
@Component
public class URITranslator {

    private Map<String,Model> translateMap = new HashMap<String, Model>();

    @Autowired
    private PagingRepositoryDelegate<Entity> repository;

    public URITranslator() {
        Model parentlearningObj = new Model(ResourceNames.LEARNINGOBJECTIVES, EntityNames.LEARNING_OBJECTIVE,
                "{version}/learningObjectives/{id}/parentLearningObjectives","parentLearningObjective", "_id");
        Model childlearningObj = new Model(ResourceNames.LEARNINGOBJECTIVES, EntityNames.LEARNING_OBJECTIVE,
                "{version}/learningObjectives/{id}/childLearningObjectives","_id", "parentLearningObjective");
        translateMap.put("parentLearningObjectives",parentlearningObj);
        translateMap.put("childLearningObjectives",childlearningObj);
    }

    public void translate(ContainerRequest request) {
        String uri = request.getPath();
        for (Map.Entry<String,Model> entry:translateMap.entrySet()) {
            String key = entry.getKey();
            if (uri.contains(key)) {
                translate(request,key);
            }
        }
    }

    private void translate(ContainerRequest request, String key) {
        Model model = translateMap.get(key);
        final UriTemplate uriTemplate = new UriTemplate(model.getPattern());
        Map<String, String> matchList = uriTemplate.match(request.getPath());
        List<String> translatedIdList = new ArrayList<String>();

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.addCriteria(new NeutralCriteria(model.getReferenceKey(), "in", Arrays.asList(matchList.get("id"))));
        neutralQuery.setOffset(0);
        neutralQuery.setLimit(0);
        Iterable<Entity> entities = repository.findAll(model.parentEntity, neutralQuery);

        for (Entity entity: entities) {
            if(model.getKey().equals("_id")) {
                translatedIdList.add(entity.getEntityId());
            } else if (entity.getBody().containsKey(model.getKey())) {
                Object value = entity.getBody().get(model.getKey());
                if (value instanceof String) {
                    translatedIdList.add((String) value);
                } else if (value instanceof List<?>) {
                    for (String id : (List<String>) value) {
                        translatedIdList.add(id);
                    }
                }
            }
        }

        String newPath = buildTranslatedPath(matchList,model.getTransformTo(), translatedIdList);
        request.setUris(request.getBaseUri(),
                request.getBaseUriBuilder().path(PathConstants.V1).path(newPath).build());
    }

    private String buildTranslatedPath(Map<String, String> matchList, String transformTo, List<String> translatedIdList) {
        return  transformTo + "/" + StringUtils.join(translatedIdList, ",");
    }

    class Model {
        String transformTo;
        String pattern;
        String parentEntity;
        String key;
        String referenceKey;

        public String getTransformTo() {
            return transformTo;
        }

        public String getParentEntity() {
            return parentEntity;
        }

        public String getPattern() {
            return pattern;
        }

        public String getKey() {
            return key;
        }

        public String getReferenceKey() {
            return referenceKey;
        }

        Model(String transformTo, String parentEntity, String pattern, String key, String referenceKey) {
            this.transformTo = transformTo;
            this.parentEntity = parentEntity;
            this.pattern = pattern;
            this.key = key;
            this.referenceKey = referenceKey;
        }
    }
}
