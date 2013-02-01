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


package org.slc.sli.ingestion.smooks;

import java.util.Collections;
import java.util.List;

/**
 * Holds all configuration information to create an instance of Smooks for SLI ingestion
 *
 * @author dduran
 *
 */
public final class SliSmooksConfig {

    private final String configFileName;

    private final List<String> targetSelectors;

    public SliSmooksConfig(String configFileName, List<String> targetSelectors) {
        this.configFileName = configFileName;
        this.targetSelectors = targetSelectors;
    }

    public String getConfigFileName() {
        return configFileName;
    }

    public List<String> getTargetSelectors() {
        return Collections.unmodifiableList(targetSelectors);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("configFileName: ");
        sb.append(this.configFileName);
        sb.append("; targetSelectors: [");
        for (String targetSelector : this.targetSelectors) {
            sb.append(targetSelector + ", ");
        }
        sb.append("]");
        return sb.toString();
    }

}
