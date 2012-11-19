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
import org.apache.commons.lang3.StringUtils;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.service.EntityNotFoundException;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriTemplate;

import java.util.*;

/**
 * This class aligns request uri with dataModel
 * @author pghosh
 *
 */
@Component
public class URITranslator {

    private Map<String,URITranlation> uriTranslationMap = new HashMap<String, URITranlation>();

    public void setRepository(PagingRepositoryDelegate<Entity> repository) {
        this.repository = repository;
    }

    @Autowired
    private PagingRepositoryDelegate<Entity> repository;

    private static final String PARENT_LEARNING_OBJECTIVE = "parentLearningObjective";
    private static final String CHILD_LEARNING_OBJECTIVE = "childLearningObjective";
    private static final String LEARNING_STANDARD = "learningStandards";
    private static final String STUDENT_COMPETENCY = "studentCompetencies";
    private static final String ID_KEY = "_id";

    public URITranslator() {
        translate(PARENT_LEARNING_OBJECTIVE).transformTo(ResourceNames.LEARNINGOBJECTIVES).
                usingPattern("{version}/learningObjectives/{id}/parentLearningObjectives").
                usingCollection(EntityNames.LEARNING_OBJECTIVE)
                .withKey(PARENT_LEARNING_OBJECTIVE).andReference(ID_KEY).build();
        translate(CHILD_LEARNING_OBJECTIVE).transformTo(ResourceNames.LEARNINGOBJECTIVES).
                usingPattern("{version}/learningObjectives/{id}/childLearningObjectives").
                usingCollection(EntityNames.LEARNING_OBJECTIVE).withKey(ID_KEY)
                .andReference(PARENT_LEARNING_OBJECTIVE).build();
        translate(LEARNING_STANDARD).transformTo(ResourceNames.LEARNINGSTANDARDS).
                usingPattern("{version}/learningObjectives/{id}/learningStandards").
                usingCollection(EntityNames.LEARNING_OBJECTIVE).withKey(LEARNING_STANDARD)
                .andReference(ID_KEY).build();
        translate(STUDENT_COMPETENCY).transformTo(ResourceNames.STUDENT_COMPETENCIES).
                usingPattern("{version}/learningObjectives/{id}/studentCompetencies").
                usingCollection(EntityNames.STUDENT_COMPETENCY).withKey("objectiveId.learningObjectiveId")
                .andReference(ID_KEY).build();
    }

    public void translate(ContainerRequest request) {
        String uri = request.getPath();
        for (Map.Entry<String,URITranlation> entry:uriTranslationMap.entrySet()) {
            String key = entry.getKey();
            if (uri.contains(key)) {
                String newPath = uriTranslationMap.get(key).translate(request.getPath());
                if (newPath.equals(uri) == false ) {
                request.setUris(request.getBaseUri(),
                        request.getBaseUriBuilder().path(PathConstants.V1).path(newPath).build());
                }
            }
        }
    }
    private URITranslationBuilder translate (String resource) {
        return new URITranslationBuilder(resource);
    }


    private class URITranslationBuilder {
        private String transformResource;
        private String transformTo;
        private String pattern;
        private String parentEntity;
        private String key;
        private String referenceKey;

        private URITranslationBuilder(String transformResource) {
            this.transformResource = transformResource;
        }
        public URITranslationBuilder transformTo (String transformTo) {
            this.transformTo = transformTo;
            return this;
        }
        public URITranslationBuilder usingPattern (String pattern) {
            this.pattern = pattern;
            return this;
        }

        public URITranslationBuilder usingCollection (String parentEntity) {
            this.parentEntity = parentEntity;
            return this;
        }

        public URITranslationBuilder withKey (String key) {
            this.key = key;
            return this;
        }

        public URITranslationBuilder andReference (String referenceKey) {
            this.referenceKey = referenceKey;
            return this;
        }

        public void build () {
            uriTranslationMap.put(transformResource,
                    new URITranlation(transformTo, pattern, parentEntity, key, referenceKey));
        }
    }
    public class URITranlation {
        String transformTo;
        String pattern;
        String parentEntity;
        String key;
        String referenceKey;

        URITranlation(String transformTo, String pattern, String parentEntity, String key, String referenceKey) {
            this.transformTo = transformTo;
            this.pattern = pattern;
            this.parentEntity = parentEntity;
            this.key = key;
            this.referenceKey = referenceKey;
        }


        public String translate(String requestPath) {
            final UriTemplate uriTemplate = new UriTemplate(pattern);
            Map<String, String> matchList = uriTemplate.match(requestPath);
            if (matchList.isEmpty()) {
                return requestPath;
            }
            List<String> translatedIdList = new ArrayList<String>();

            NeutralQuery neutralQuery = new NeutralQuery();
            neutralQuery.addCriteria(new NeutralCriteria(referenceKey, "in", Arrays.asList(matchList.get("id"))));
            neutralQuery.setOffset(0);
            neutralQuery.setLimit(0);
            Iterable<Entity> entities = repository.findAll(parentEntity, neutralQuery);

            for (Entity entity: entities) {
                if(key.equals("_id")) {
                    translatedIdList.add(entity.getEntityId());
                } else if (entity.getBody().containsKey(key)) {
                    Object value = entity.getBody().get(key);
                    if (value instanceof String) {
                        translatedIdList.add((String) value);
                    } else if (value instanceof List<?>) {
                        for (String id : (List<String>) value) {
                            translatedIdList.add(id);
                        }
                    }
                }
            }
            if (translatedIdList.isEmpty()) {
                throw new EntityNotFoundException("Could not locate entity.");
            }
            return buildTranslatedPath(translatedIdList);
        }

        private String buildTranslatedPath(  List<String> translatedIdList) {
            return  transformTo + "/" + StringUtils.join(translatedIdList, ",");
        }

    }
    public URITranlation getTranslator(String uri) {
        return uriTranslationMap.get(uri);
    }
}
