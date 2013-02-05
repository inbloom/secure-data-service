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


package org.slc.sli.ingestion.transformation.normalization;

import java.util.List;

/**
 * Holds definition for complex external reference such as CourseReferenceType that needs to be resolved.
 *
 * @author slee
 *
 */
public class ComplexRefDef {
    private String fieldPath;
    private String entityType;
    private String path;
    private String valueSource;
    private List<String> complexFieldNames;

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getValueSource() {
        return valueSource;
    }

    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
    }

    public List<String> getComplexFieldNames() {
        return complexFieldNames;
    }

    public void setComplexFieldNames(List<String> complexFieldNames) {
        this.complexFieldNames = complexFieldNames;
    }
}

