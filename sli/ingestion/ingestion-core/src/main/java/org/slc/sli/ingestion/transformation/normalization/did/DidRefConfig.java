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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Contains the per-referenceType configuration for deterministic Id resolution.
 *
 * @author jtully
 *
 */
public class DidRefConfig {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String entityType;
    private List<KeyFieldDef> keyFields;

    public DidRefConfig() {
        keyFields = new ArrayList<KeyFieldDef>();
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public List<KeyFieldDef> getKeyFields() {
        return keyFields;
    }

    public void setKeyFields(List<KeyFieldDef> keyFields) {
        this.keyFields = keyFields;
    }

    public static DidRefConfig parse(InputStream inputStream) throws IOException {
        return MAPPER.readValue(inputStream, DidRefConfig.class);
    }

    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (java.io.IOException e) {
            return super.toString();
        }
    }

}
