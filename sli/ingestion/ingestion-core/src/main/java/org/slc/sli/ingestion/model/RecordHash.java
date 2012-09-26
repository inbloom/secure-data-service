package org.slc.sli.ingestion.model;

import org.springframework.data.mongodb.core.index.Indexed;


public class RecordHash {

    public String recordId;
    
    public String timestamp;

    @Indexed
    public String tenantId;

    public RecordHash() {
        this.recordId = "";
        this.timestamp = "";
        this.tenantId = "";
    }

}
