package org.slc.sli.ingestion;

import java.util.HashMap;
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
    private Map<String, Object> metaData;  // Added 2/2/2012 by Thomas Shewchuk
    private String entityId;  // Added 2/2/2012 by Thomas Shewchuk

    public NeutralRecordEntity(NeutralRecord neutralRecord) {
        this(neutralRecord, 0);
    }

    /**
     * @author tshewchuk 1/12/2010 (PI2 US311)
     */
    public NeutralRecordEntity(NeutralRecord neutralRecord, int recordNumberInFile) {
        this.neutralRecord = neutralRecord;
        this.recordNumberInFile = recordNumberInFile;  // Added 2/2/2012 by Thomas Shewchuk
        this.metaData = new HashMap<String, Object>();  // Added 2/7/2012 by Thomas Shewchuk
        this.entityId = null;  // Added 2/7/2012 by Thomas Shewchuk
    }

    @Override
    public String getType() {
        return neutralRecord.getRecordType();
    }

    @Override
    public String getEntityId() {
        return entityId;  // Modified 2/7/2012 by Thomas Shewchuk
    }

    @Override
    public Map<String, Object> getBody() {
        return neutralRecord.getAttributes();
    }

    /**
     * @author tshewchuk 2/2/2010 (PI3 US811)
     */
    @Override
    public Map<String, Object> getMetaData() {
        return metaData;
    }

    /**
     * @author tshewchuk 2/2/2010 (PI3 US811)
     */
    public void setMetaDataField(String fieldName, Object fieldValue) {
        if (metaData.containsKey(fieldName))
            metaData.remove(fieldName);
        metaData.put(fieldName, fieldValue);
    }

    /**
     * @author tshewchuk 2/6/2010 (PI3 US811)
     */
    public void setAttributeField(String fieldName, Object fieldValue) {
        this.neutralRecord.setAttributeField(fieldName, fieldValue);
    }

    public Object getLocalId() {
        return neutralRecord.getLocalId();
    }

    /**
     * @author tshewchuk 2/3/2010 (PI3 US811)
     */
    public Map<String, Object> getLocalParentIds() {
        return neutralRecord.getLocalParentIds();
    }

    /**
     * @author tshewchuk 1/12/2010 (PI3 US811)
     */
    public int getRecordNumberInFile() {
        return recordNumberInFile;
    }

    /**
     * @author tshewchuk 2/1/2010 (PI3 US811)
     */
    public boolean isAssociation() {
        return neutralRecord.isAssociation();
    }

    /**
     * @author tshewchuk 2/7/2010 (PI3 US811)
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

}
