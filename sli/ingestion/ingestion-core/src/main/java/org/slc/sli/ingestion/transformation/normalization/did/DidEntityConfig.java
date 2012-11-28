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

package org.slc.sli.ingestion.transformation.normalization.did;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * Contains the per-source-entity configuration for deterministic Id resolution.
 *
 * @author jtully
 *
 */
public class DidEntityConfig {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private List<DidRefSource> referenceSources;

    public DidEntityConfig() {
        referenceSources = new ArrayList<DidRefSource>();
    }

    public List<DidRefSource> getReferenceSources() {
        return referenceSources;
    }

    public void setReferenceSources(List<DidRefSource> referenceSources) {
        this.referenceSources = referenceSources;
    }

    public static DidEntityConfig parse(InputStream inputStream) throws IOException {
        return MAPPER.readValue(inputStream, DidEntityConfig.class);
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
