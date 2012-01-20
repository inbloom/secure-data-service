package org.slc.sli.ingestion;

import java.util.Map;

import org.slc.sli.domain.Entity;

/**
 * Adapter for Entity
 *
 * @author dduran
 *
 */
public class NeutralRecordEntity implements Entity {

    private final NeutralRecord neutralRecord;

    private int recordNumberInFile;

    public NeutralRecordEntity(NeutralRecord neutralRecord) {
        this.neutralRecord = neutralRecord;
    }

    public NeutralRecordEntity(NeutralRecord neutralRecord, int recordNumberInFile) {
        this.neutralRecord = neutralRecord;
        this.recordNumberInFile = recordNumberInFile;
    }

    @Override
    public String getType() {
        return neutralRecord.getRecordType();
    }

    @Override
    public String getEntityId() {
        return null;
    }

    @Override
    public Map<String, Object> getBody() {
        return neutralRecord.getAttributes();
    }

    public int getRecordNumberInFile() {
        return recordNumberInFile;
    }

}
