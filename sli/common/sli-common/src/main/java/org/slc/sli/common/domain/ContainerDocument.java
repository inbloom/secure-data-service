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

package org.slc.sli.common.domain;

import java.util.Collections;
import java.util.List;

/**
 * @author pghosh
 */
public final class ContainerDocument {
    private final String collectionName;
    private final List<String> parentNaturalKeys;


    private final List<String> containerDocNaturalKeys;
    private final String fieldToPersist;
    private final String collectionToPersist;
    private boolean isContainerSubdoc;

    public String getCollectionName() {
        return collectionName;
    }

    public List<String> getParentNaturalKeys() {
        return Collections.unmodifiableList(parentNaturalKeys);
    }

    public String getFieldToPersist() {
        return fieldToPersist;
    }


    public String getCollectionToPersist() {
        return collectionToPersist;
    }

    public boolean isContainerSubdoc() {
        return isContainerSubdoc;
    }

    public List<String> getContainerDocNaturalKeys() {
        return containerDocNaturalKeys;
    }

    public static ContainerDocumentBuilder builder() {
        return new ContainerDocumentBuilder();
    }

    private ContainerDocument(final ContainerDocumentBuilder builder) {
        this.collectionName = builder.collectionName;
        this.parentNaturalKeys = builder.parentNaturalKeys;
        this.fieldToPersist = builder.fieldToPersist;
        this.collectionToPersist = builder.collectionToPersist;
        this.isContainerSubdoc = builder.isContainerSubdoc;
        this.containerDocNaturalKeys =builder.containerDocNaturalKeys;
    }

    /**
     * @author pghosh
     */
    public static final class ContainerDocumentBuilder {
        private String collectionName;
        private List<String> parentNaturalKeys;
        private List<String> containerDocNaturalKeys;
        private String fieldToPersist;
        private String collectionToPersist;
        private boolean isContainerSubdoc;

        public ContainerDocumentBuilder forCollection(final String collectionName) {
            this.collectionName = collectionName;
            return this;
        }

        public ContainerDocumentBuilder withParent(final List<String> parent) {
            this.parentNaturalKeys = parent;
            return this;
        }

        public ContainerDocumentBuilder withContainerDocKeys(final List<String> containerDocKeys) {
            this.containerDocNaturalKeys = containerDocKeys;
            return this;
        }

        public ContainerDocumentBuilder forField(final String fieldName) {
            this.fieldToPersist = fieldName;
            return this;
        }
        public ContainerDocumentBuilder persistAs(final String collectionToPersist) {
            this.collectionToPersist = collectionToPersist;
            return this;
        }

        public ContainerDocumentBuilder asContainerSubdoc(final boolean isContainerSubdoc) {
            this.isContainerSubdoc = isContainerSubdoc;
            return this;
        }

        public ContainerDocument build() {
            if (collectionName == null || parentNaturalKeys == null || fieldToPersist == null || collectionToPersist==null) {
                throw new IllegalStateException("The container document is not fully initialized!");
            }
            return new ContainerDocument(this);
        }
    }
}

