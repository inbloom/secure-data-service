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

package org.slc.sli.bulk.extract.extractor;

import org.joda.time.DateTime;

import java.io.File;

/**
 * Extract the Public Data for the State Education Agency
 * ablum
 */
public class StatePublicDataExtractor {

    /**
     * Creates unencrypted SEA public data bulk extract files if any are needed for the given tenant
     * @param tenant name of tenant to extract
     */
    public void execute(String tenant, File tenantDirectory, DateTime startTime) {
        String seaId = null;

        extractPublicData(seaId);
    }

    /**
     *
     * @param seaId the ID of the SEA to extract
     */
    private boolean extractPublicData(String seaId) {
        return false;
    }
}
