package org.slc.sli.ingestion.transformation.normalization.did;

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

import java.util.List;

/**
 * Configures how a referenceType objects maps to keyFields for
 * deterministic Id resolution.
 *
 * @author jtully
 *
 */
public class KeyFieldDef {
    //SLI name of the key field
    String keyFieldName;

    //source x-path (relative to the referenceType object) to the keyfield values
    List<String> valueSources;

    //optional nested deterministic Id reference configuration
    DidRefConfig refConfig;

    public String getKeyFieldName() {
        return keyFieldName;
    }
    public void setKeyFieldName(String keyFieldName) {
        this.keyFieldName = keyFieldName;
    }
    public List<String> getValueSources() {
        return valueSources;
    }
    public void setValueSource(List<String> valueSources) {
        this.valueSources = valueSources;
    }
    public DidRefConfig getRefConfig() {
        return refConfig;
    }
    public void setRefConfig(DidRefConfig refConfig) {
        this.refConfig = refConfig;
    }
}
