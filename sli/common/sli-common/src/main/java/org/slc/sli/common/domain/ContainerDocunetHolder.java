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

import java.util.List;
import java.util.Map;

/**
 * @author pghosh
 */
public class ContainerDocunetHolder {
    private List<ContainerDocument> containerDocumentList;

    public ContainerDocunetHolder() {
        this.containerDocumentList = null;
    }

    public class ContainerDocument {
    private final String collectionName;
    private final Map<String, String> parentNaturalKeyMap;
    private final String fieldToPersist;

    private ContainerDocument(String collectionName, Map<String, String> parentNaturalKeyMap, String fieldToPersist) {
        this.collectionName = collectionName;
        this.parentNaturalKeyMap = parentNaturalKeyMap;
        this.fieldToPersist = fieldToPersist;
    }

    private class ContainerDocumentBuilder {
        private String collectionName;
        private Map<String, String> parentNaturalKeyMap;
        private String fieldToPersist;

        public ContainerDocumentBuilder forCollection(String collectionName) {
            this.collectionName = collectionName;
            return this;
        }

        public ContainerDocumentBuilder withParent(Map<String, String> parent) {
            this.parentNaturalKeyMap = parent;
            return this;
        }

        public ContainerDocumentBuilder forField(String fieldName) {
            this.fieldToPersist = fieldName;
            return this;
        }

        public ContainerDocument build() {
            return new ContainerDocument(collectionName, parentNaturalKeyMap, fieldToPersist);
        }

    }
}
}

