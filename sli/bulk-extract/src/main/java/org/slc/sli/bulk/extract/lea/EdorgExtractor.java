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

package org.slc.sli.bulk.extract.lea;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

import java.util.ArrayList;
import java.util.HashSet;

public class EdorgExtractor implements EntityExtract {
    private LEAExtractFileMap map;
    private EntityExtractor extractor;
    
    public EdorgExtractor(EntityExtractor extractor, LEAExtractFileMap map) {
        this.extractor = extractor;
        this.map = map;
    }

    /* (non-Javadoc)
     * @see org.slc.sli.bulk.extract.lea.EntityExtract#extractEntities(java.util.Map)
     */
    @Override
    public void extractEntities(EntityToLeaCache entityToEdorgCache) {
        for (String lea : new HashSet<String>(entityToEdorgCache.getEntityIds())) {
            SecurityEvent event = SecurityEventUtil.createSecurityEvent(this.getClass().getName(), "Extracting edOrg data for top level LEA" , "EdOrg data extract initiated", LogLevelType.TYPE_INFO);
            event.setTargetEdOrg(lea);
            audit(event);
            ExtractFile extractFile = map.getExtractFileForLea(lea);
            NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id",
                    NeutralCriteria.CRITERIA_IN, new ArrayList<String>(entityToEdorgCache.getEntriesById(lea))));
            extractor.setExtractionQuery(query);
            extractor.extractEntities(extractFile, "educationOrganization");
        }
    }

}
