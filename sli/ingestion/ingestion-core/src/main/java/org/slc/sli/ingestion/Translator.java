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

package org.slc.sli.ingestion;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;

/**
 * Utility class for representation conversions routines. Used to translate among
 * the various object representations including XML, JSON, AVRO Neutral Record.
 *
 */

@Component
public final class Translator {

    private Translator() { }

    private static final Logger LOG = LoggerFactory.getLogger(Translator.class);

    /**
     * Converts the SLI Ingestion neutral record to a SLI Domain instance
     *
     * @param neutralRecord
     * @return
     */
    public static NeutralRecordEntity mapToEntity(NeutralRecord neutralRecord, long recordNumber) {
        LOG.debug("converting NeutralRecord({}) to NeutralRecordEntity", recordNumber);
        return new NeutralRecordEntity(neutralRecord, recordNumber);
    }

    /**
     * Converts the SLI Domain instance to a SLI Ingestion neutral record
     *
     * @param neutralRecord
     * @return
     */
    public static NeutralRecord mapToNeutralRecord(Entity instance) {
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setRecordType(instance.getType());

        Map<String, Object> attributes = new HashMap<String, Object>();
        Map<String, Object> body = instance.getBody();

        attributes.putAll(body);

        neutralRecord.setAttributes(attributes);

        return neutralRecord;
    }

}
