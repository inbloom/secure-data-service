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

import java.util.Map;

/**
 *
 * @author pghosh
 */
public class ContainerDocument {
    private  String collectionName;
    private  Map<String, String> parentNaturalKeyMap;
    private  String fieldToPersist;

    public String getCollectionName() {
        return collectionName;
    }

    private void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Map<String, String> getParentNaturalKeyMap() {
        return parentNaturalKeyMap;
    }

    private void setParentNaturalKeyMap(Map<String, String> parentNaturalKeyMap) {
        this.parentNaturalKeyMap = parentNaturalKeyMap;
    }

    public String getFieldToPersist() {
        return fieldToPersist;
    }

    private void setFieldToPersist(String fieldToPersist) {
        this.fieldToPersist = fieldToPersist;
    }

    /**
     *
     * @author pghosh
     */
    public static class ContainerDocumentBuilder {
        private final ContainerDocument containerDocument;

        public ContainerDocumentBuilder() {
            this.containerDocument = new ContainerDocument();
        }

        public ContainerDocumentBuilder forCollection(final String collectionName) {
            containerDocument.setCollectionName(collectionName);
            return this;
        }

        public ContainerDocumentBuilder withParent(final Map<String, String> parent) {
            containerDocument.setParentNaturalKeyMap(parent);
            return this;
        }

        public ContainerDocumentBuilder forField(final String fieldName) {
            containerDocument.setFieldToPersist(fieldName);
            return this;
        }

        /**
         *
         * @return null if not fully initialized
         */
        public ContainerDocument build() {
            if (containerDocument.getCollectionName() != null &&
                    containerDocument.getFieldToPersist() != null &&
                    containerDocument.getParentNaturalKeyMap() != null) {
                return containerDocument;
            } else {
                return null;
            }
        }
    }
}

