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

package org.slc.sli.api.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import javax.ws.rs.core.PathSegment;


/**
 * This class aligns request uri with dataModel
 * @author pghosh
 *
 */
@Component
public class URITranslator {

    private Map<String, URITranslation> uriTranslationMap = new HashMap<String, URITranslation>();

    public void setRepository(PagingRepositoryDelegate<Entity> repository) {
        this.repository = repository;
    }

    @Autowired
    private PagingRepositoryDelegate<Entity> repository;

    private static final String PARENT_LEARNING_OBJECTIVE = "parentLearningObjective";
    private static final String CHILD_LEARNING_OBJECTIVE = "childLearningObjective";
    private static final String LEARNING_STANDARD = "learningStandards";
    private static final String STUDENT_COMPETENCY = "studentCompetencies";
    private static final String COURSE_TRANSCRIPT = "courseTranscript";
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
        translate(COURSE_TRANSCRIPT).transformToUri("studentAcademicRecords/{id}/courseTranscripts").
                usingPattern("{version}/students/{id}/courseTranscripts").
                ignoringPattern(Arrays.asList("/students/studentAcademicRecords/courseTranscripts")).
                usingCollection(EntityNames.STUDENT_ACADEMIC_RECORD).withKey(ID_KEY)
                .andReference("studentId").build();
    }

    public void translate(ContainerRequest request) {
        String uri = request.getPath();
        List<PathSegment> segments = request.getPathSegments();
        String version = PathConstants.V1;

        if (!segments.isEmpty()) {
            version = segments.get(0).getPath();
        }

        for (Map.Entry<String, URITranslation> entry : uriTranslationMap.entrySet()) {
            String key = entry.getKey();
            if (uri.contains(key)) {
                String newPath = uriTranslationMap.get(key).translate(request.getPath());
                if (!newPath.equals(uri)) {
                    request.setUris(request.getBaseUri(),
                        request.getBaseUriBuilder().path(version).path(newPath).build());
                }
            }
        }
    }
    private URITranslationBuilder translate(String resource) {
        return new URITranslationBuilder(resource);
    }

    /**
     * A builder for translating URI(s) to equivalent URI(s).
     * 
     * @author kmyers
     *
     */
    private final class URITranslationBuilder {
        private String transformResource;
        private String transformTo;
        private String pattern;
        private String parentEntity;
        private String key;
        private String referenceKey;
        private String transformToUri;
        private List<String> ignorePatternList;

        private URITranslationBuilder(String transformResource) {
            this.transformResource = transformResource;
        }

        public URITranslationBuilder transformTo(String transformTo) {
            this.transformTo = transformTo;
            return this;
        }

        public URITranslationBuilder transformToUri(String transformToUri) {
            this.transformToUri = transformToUri;
            return this;
        }

        public URITranslationBuilder usingPattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public URITranslationBuilder usingCollection(String parentEntity) {
            this.parentEntity = parentEntity;
            return this;
        }

        public URITranslationBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public URITranslationBuilder andReference(String referenceKey) {
            this.referenceKey = referenceKey;
            return this;
        }

        public URITranslationBuilder ignoringPattern(List<String> ignoreList) {
            this.ignorePatternList = ignoreList;
            return this;
        }

        public void build() {
            uriTranslationMap.put(transformResource,
                    new URITranslation(transformTo, pattern, parentEntity, key, referenceKey, transformToUri, ignorePatternList));
        }
    }
    
    /**
     * Encapsulates the conversion/translation from one URI to an equivalent one.
     * 
     * @author kmyers
     *
     */
    public class URITranslation {
        String transformTo;
        String pattern;
        String parentEntity;
        String key;
        String referenceKey;
        String transformToUri;
        List<String> ignorePatternList;

        URITranslation(String transformTo, String pattern, String parentEntity, String key,
                       String referenceKey, String transformToUri, List<String> ignorePatternList) {
            this.transformTo = transformTo;
            this.pattern = pattern;
            this.parentEntity = parentEntity;
            this.key = key;
            this.referenceKey = referenceKey;
            this.transformToUri = transformToUri;
            this.ignorePatternList = ignorePatternList;
        }


        public String translate(String requestPath) {
            final UriTemplate uriTemplate = new UriTemplate(pattern);
            Map<String, String> matchList = uriTemplate.match(requestPath);
            if (matchList.isEmpty() || ignorePatternList(requestPath)) {
                return requestPath;
            }
            List<String> translatedIdList = new ArrayList<String>();

            NeutralQuery neutralQuery = new NeutralQuery();
            neutralQuery.addCriteria(new NeutralCriteria(referenceKey, "in", Arrays.asList(matchList.get("id"))));
            neutralQuery.setOffset(0);
            neutralQuery.setLimit(0);
            Iterable<Entity> entities = repository.findAll(parentEntity, neutralQuery);

            for (Entity entity : entities) {
                if (key.equals("_id")) {
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

        private boolean ignorePatternList(String requestPath) {
            if(ignorePatternList != null && ignorePatternList.isEmpty()!=true) {
                for(String pattern: ignorePatternList) {
                    if(requestPath.contains(pattern)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private String buildTranslatedPath(List<String> translatedIdList) {
            if (transformToUri != null) {
                return transformToUri.replaceAll("\\{id\\}", StringUtils.join(translatedIdList, ","));
            } else {
                return  transformTo + "/" + StringUtils.join(translatedIdList, ",");
            }
        }

    }
    public URITranslation getTranslator(String uri) {
        return uriTranslationMap.get(uri);
    }
}
