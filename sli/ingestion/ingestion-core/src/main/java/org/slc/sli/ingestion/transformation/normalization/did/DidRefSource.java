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

package org.slc.sli.ingestion.transformation.normalization.did;

/**
 * Configures which referenceType objects map to which Id fields for
 * deterministic Id resolution.
 *
 * @author jtully
 *
 */
public class DidRefSource {
    //the mapped entity type
    private String entityType;

    //Ed-Fi x-path defining the location of ReferenceType in the source data
    private String sourceRefPath;

    //is the reference optional?
    private boolean optional = false;

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getSourceRefPath() {
        return sourceRefPath;
    }

    public void setSourceRefPath(String sourceRefPath) {
        this.sourceRefPath = sourceRefPath;
    }

    @Override
    public String toString() {
        return "DidRefSource [entityType=" + entityType + ", sourceRefPath=" + sourceRefPath + ", optional=" + optional
                + "]";
    }
}
