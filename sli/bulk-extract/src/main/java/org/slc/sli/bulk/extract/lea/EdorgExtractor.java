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

import java.util.ArrayList;
import java.util.HashSet;

import org.slc.sli.bulk.extract.extractor.EntityExtractor;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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
            ExtractFile extractFile = map.getExtractFileForLea(lea);
            Criteria criteria = new Criteria("_id");
            criteria.in(new ArrayList<String>(entityToEdorgCache.getEntriesById(lea)));
            Query query = new Query(criteria);
            extractor.setExtractionQuery(query);
            extractor.extractEntities(extractFile, "educationOrganization");
        }
    }

}
