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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Configuration of keys and references for an entity.
 *
 * @author okrook
 *
 */
public class EntityConfig {
    private List<String> keyFields;
    private List<RefDef> references;
    private ComplexRefDef complexReference;
    private ComplexKeyField complexKeyField;
    private Map<String, Object> piiFields;
    private Map<String, String> subEntities;

    public List<String> getKeyFields() {
        return keyFields;
    }

    public void setKeyFields(List<String> keyFields) {
        this.keyFields = keyFields;
    }

    public List<RefDef> getReferences() {
        return references;
    }

    public void setReferences(List<RefDef> references) {
        this.references = references;
    }

    public ComplexRefDef getComplexReference() {
        return complexReference;
    }

    public void setComplexReference(ComplexRefDef reference) {
        this.complexReference = reference;
    }

    public void setPiiFields(Map<String, Object> piiFields) {
        this.piiFields = piiFields;
    }

    public Map<String, Object> getPiiFields() {
        return piiFields;
    }

    public Map<String, String> getSubEntities() {
        return subEntities;
    }

    public void setSubEntities(Map<String, String> subEntities) {
        this.subEntities = subEntities;
    }

    public static EntityConfig parse(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, EntityConfig.class);
    }

    public ComplexKeyField getComplexKeyField() {
        return complexKeyField;
    }

    public void setComplexKeyField(ComplexKeyField complexKeyField) {
        this.complexKeyField = complexKeyField;
    }

}
