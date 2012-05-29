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
public class Translator {

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

        for (String key : body.keySet()) {
            attributes.put(key, body.get(key));
        }

        neutralRecord.setAttributes(attributes);

        return neutralRecord;
    }

}
