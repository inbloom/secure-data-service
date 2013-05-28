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
import org.slc.sli.bulk.extract.message.BEMessageCode;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;

public class EdorgExtractor implements EntityExtract {
    private LEAExtractFileMap map;
    private EntityExtractor extractor;
    private LocalEdOrgExtractHelper localEdOrgExtractHelper;

    @Autowired
    private SecurityEventUtil securityEventUtil;
    
    public EdorgExtractor(EntityExtractor extractor, LEAExtractFileMap map, LocalEdOrgExtractHelper localEdOrgExtractHelper) {
        this.extractor = extractor;
        this.map = map;
        this.localEdOrgExtractHelper = localEdOrgExtractHelper;
    }

    /* (non-Javadoc)
     * @see org.slc.sli.bulk.extract.lea.EntityExtract#extractEntities(java.util.Map)
     */
    @Override
    public void extractEntities(EntityToLeaCache entityToEdorgCache) {
        localEdOrgExtractHelper.logSecurityEvent(map.getLeas(), EntityNames.EDUCATION_ORGANIZATION, this.getClass().getName());
        for (String lea : new HashSet<String>(entityToEdorgCache.getEntityIds())) {
            ExtractFile extractFile = map.getExtractFileForLea(lea);
            NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id",
                    NeutralCriteria.CRITERIA_IN, new ArrayList<String>(entityToEdorgCache.getEntriesById(lea))));
            extractor.setExtractionQuery(query);
            extractor.extractEntities(extractFile, "educationOrganization");
        }
    }

    /**
     * Set securityEventUtil.
     * @param securityEventUtil the securityEventUtil to set
     */
    public void setSecurityEventUtil(SecurityEventUtil securityEventUtil) {
        this.securityEventUtil = securityEventUtil;
    }

}
