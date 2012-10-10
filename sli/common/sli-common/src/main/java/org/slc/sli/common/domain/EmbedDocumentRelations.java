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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Maps subdocument to their parents
 *
 * @author ycao
 *
 */
public class EmbedDocumentRelations {

    private static final Map<String, Parent> SUBDOC_TO_PARENT;

    static {
        Map<String, Parent> map = new HashMap<String, Parent>();
        map.put("studentSectionAssociation", new Parent("section", "sectionId"));
        // map.put("studentAssessmentAssociation", new Parent("student", "studentId"));
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
}
