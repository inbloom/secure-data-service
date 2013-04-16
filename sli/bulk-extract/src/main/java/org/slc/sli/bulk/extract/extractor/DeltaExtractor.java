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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.bulk.extract.delta.DeltaEntityIterator;
import org.slc.sli.bulk.extract.delta.DeltaEntityIterator.DeltaRecord;

@Component
public class DeltaExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(DeltaExtractor.class);
    
    @Autowired
    DeltaEntityIterator deltaEntityIterator;

    public void execute(String tenant, DateTime deltaUptoTime) {
        deltaEntityIterator.init(tenant, deltaUptoTime);
        
        while (deltaEntityIterator.hasNext()) {
            DeltaRecord delta = deltaEntityIterator.next();
            LOG.info(String.format("entity %s belongs to: %s has been %s", delta.getEntity().toString(), delta.getBelongsToLEA().toString(), delta.getOp().toString()));
        }
    }
}
