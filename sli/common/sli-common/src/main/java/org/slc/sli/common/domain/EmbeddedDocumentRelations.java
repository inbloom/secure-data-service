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

package org.slc.sli.common.domain;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maps subdocuments to their parents
 *
 * @author ycao
 *
 */
public class EmbeddedDocumentRelations {

    private static final Map<String, Parent> SUBDOC_TO_PARENT;
    private static final Map<String, Denormalization> DENORMALIZATIONS;

    static {
        Map<String, Parent> map = new HashMap<String, Parent>();
        Map<String, Denormalization> denormalizationMap = new HashMap<String, Denormalization>();

        Map<String, String> sectionReferenceMap = new HashMap<String, String>();
        sectionReferenceMap.put("studentId", "_id");
        denormalizationMap.put("studentSectionAssociation", new Denormalization("student", "section", sectionReferenceMap,
                "sectionId", Arrays.asList("endDate")));
        //Student--program
        denormalizationMap.put("studentProgramAssociation", new Denormalization("student", "program", sectionReferenceMap,
                "programId", Arrays.asList("endDate")));
        DENORMALIZATIONS = Collections.unmodifiableMap(denormalizationMap);

        map.put("studentSectionAssociation", new Parent("section", "sectionId"));
        map.put("studentAssessmentAssociation", new Parent("student", "studentId"));
        map.put("gradebookEntry", new Parent("section", "sectionId"));
        map.put("teacherSectionAssociation", new Parent("section", "sectionId"));
        map.put("studentProgramAssociation", new Parent("program", "programId"));
        SUBDOC_TO_PARENT = Collections.unmodifiableMap(map);
    };

    public static Set<String> getSubDocuments() {
        return SUBDOC_TO_PARENT.keySet();
    }

    public static String getParentFieldReference(String entityType) {
        Parent parent = getParent(entityType);
        return (parent != null ? parent.getParentReferenceFieldName() : null);
    }

    public static String getParentEntityType(String entityType) {
        Parent parent = getParent(entityType);
        return (parent != null ? parent.getParentEntityType() : null);
    }

    private static Parent getParent(String entityType) {
        if (!SUBDOC_TO_PARENT.containsKey(entityType)) {
            return null;
        }

        return SUBDOC_TO_PARENT.get(entityType);
    }

    public static Set<String> getDenormalizedDocuments() {
        return DENORMALIZATIONS.keySet();
    }

    public static String getDenormalizeToEntity(String entityType) {
        Denormalization denormalization = getDenormalization(entityType);
        return (denormalization != null ? denormalization.getDenormalizeToEntity() : null);
    }

    public static Map<String, String> getReferenceKeys(String entityType) {
        Denormalization denormalization = getDenormalization(entityType);
        return (denormalization != null ? denormalization.getReferenceKeys() : null);
    }

    public static String getDenormalizedIdKey(String entityType) {
        Denormalization denormalization = getDenormalization(entityType);
        return (denormalization != null ? denormalization.getDenormalizedIdKey() : null);
    }

    public static List<String> getDenormalizedFields(String entityType) {
        Denormalization denormalization = getDenormalization(entityType);
        return (denormalization != null ? denormalization.getDenormalizedFields() : null);
    }

    public static String getDenormalizedToField(String entityType) {
        Denormalization denormalization = getDenormalization(entityType);
        return (denormalization != null ? denormalization.getDenormalizedToField() : null);
    }

    private static Denormalization getDenormalization(String entityType) {
        if (!DENORMALIZATIONS.containsKey(entityType)) {
            return null;
        }

        return DENORMALIZATIONS.get(entityType);
    }

    private static class Parent {

        private String parentEntityType;
        private String parentReferenceFieldName;

        public Parent(String parentEntityType, String parentReferenceFieldName) {
            this.parentEntityType = parentEntityType;
            this.parentReferenceFieldName = parentReferenceFieldName;
        }

        public String getParentEntityType() {
            return parentEntityType;
        }

        public String getParentReferenceFieldName() {
            return parentReferenceFieldName;
        }
    }

    private static class Denormalization {

        private String denormalizeToEntity;
        private Map<String, String> referenceKeys;
        private String denormalizedIdKey;
        private List<String> denormalizedFields;
        private String denormalizedToField;

        public Denormalization(String denormalizeToEntity, String denormalizedToField, Map<String, String> referenceKeys,
                               String denormalizedIdKey, List<String> denormalizedFields) {
            this.denormalizeToEntity = denormalizeToEntity;
            this.denormalizedToField = denormalizedToField;
            this.referenceKeys = referenceKeys;
            this.denormalizedIdKey = denormalizedIdKey;
            this.denormalizedFields = denormalizedFields;
        }

        public String getDenormalizeToEntity() {
            return denormalizeToEntity;
        }

        public Map<String, String> getReferenceKeys() {
            return referenceKeys;
        }

        public String getDenormalizedIdKey() {
            return denormalizedIdKey;
        }

        public List<String> getDenormalizedFields() {
            return denormalizedFields;
        }

        public String getDenormalizedToField() {
            return denormalizedToField;
        }
    }
}
