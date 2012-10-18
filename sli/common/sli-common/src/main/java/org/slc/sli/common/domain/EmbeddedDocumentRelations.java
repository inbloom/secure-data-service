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

    private static final Map<String, List<Parent>> SUBDOC_TO_PARENT;

    static {
        Map<String, List<Parent>> map = new HashMap<String, List<Parent>>();
        Parent embedCompletelyOnSection = new Parent("section", "sectionId");
        Parent embedCompletelyOnStudent = new Parent("student", "studentId");
        map.put("studentSectionAssociation", Arrays.asList(embedCompletelyOnSection));
        map.put("studentAssessmentAssociation", Arrays.asList(embedCompletelyOnStudent));
        SUBDOC_TO_PARENT = Collections.unmodifiableMap(map);
    };

    public static Set<String> getSubDocuments() {
        return SUBDOC_TO_PARENT.keySet();
    }

    public static List<Parent> getParents(String entityType) {
        return SUBDOC_TO_PARENT.get(entityType);
    }

    /**
     * Holds information about the parent of an embedded sub-document.
     */
    public static class Parent {
        private String parentEntityType;
        private String parentReferenceFieldName;
        private Map<String, String> fieldMapping;
        private boolean remainsFirstClassEntity;

        /**
         * Constructor used when an entity is to be wholly embedded on another object.
         *
         * @param parentEntityType
         *            Type of the parent entity.
         * @param parentReferenceFieldName
         *            Field used to match sub-document with parent document.
         */
        public Parent(String parentEntityType, String parentReferenceFieldName) {
            this.parentEntityType = parentEntityType;
            this.parentReferenceFieldName = parentReferenceFieldName;
            this.fieldMapping = new HashMap<String, String>();
            this.fieldMapping.put(parentReferenceFieldName, "_id");
            this.remainsFirstClassEntity = false;
        }

        /**
         * Constructor used when pieces of an entity are to be de-normalized on another object.
         *
         * @param parentEntityType
         *            Type of the parent entity.
         * @param parentReferenceFieldName
         *            Field used to match sub-document with parent document.
         * @param fieldsToBeMapped
         *            Map containing entries { Original document field (key) --> Embedded
         *            sub-document field (key) }
         */
        public Parent(String parentEntityType, String parentReferenceFieldName, Map<String, String> fieldsToBeMapped) {
            this.parentEntityType = parentEntityType;
            this.parentReferenceFieldName = parentReferenceFieldName;
            this.fieldMapping = fieldsToBeMapped;
            this.remainsFirstClassEntity = true;
        }

        public String getParentEntityType() {
            return parentEntityType;
        }

        public String getParentReferenceFieldName() {
            return parentReferenceFieldName;
        }

        public Map<String, String> getFieldMapping() {
            return fieldMapping;
        }

        public boolean getRemainsFirstClassEntity() {
            return remainsFirstClassEntity;
        }
    }
}
