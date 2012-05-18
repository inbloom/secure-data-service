package org.slc.sli.ingestion.transformation;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.ingestion.NeutralRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Transformation strategy to persist data as-is, no actual transformation
 * 
 * @author vmcglaughlin
 */
@Scope("prototype")
@Component("PassThroughTransformationStrategy")
public class PassThroughTransformationStrategy extends AbstractTransformationStrategy {
    
    private static final Logger LOG = LoggerFactory.getLogger(PassThroughTransformationStrategy.class);
    
    private Map<Object, NeutralRecord> collection;
    
    private String passThroughCollectionName;
    
    /**
     * Default constructor.
     */
    public PassThroughTransformationStrategy() {
        this.collection = new HashMap<Object, NeutralRecord>();
    }
    
    /**
     * The chaining of transformation steps. This implementation forces that all data will be
     * processed in "one-go"
     */
    @Override
    public void performTransformation() {
        loadData();
        transform();
        persist();
    }
    
    /**
     * There are no pre-requisite interchanges for an entity that is undergoing a passthrough
     * transformation.
     */
    private void loadData() {
        if (passThroughCollectionName == null || passThroughCollectionName.isEmpty()) {
            LOG.warn("Collection to pass through not specified, null or empty");
            return;
        }
        
        LOG.info("Loading data for passthrough transformation for {}.", passThroughCollectionName);
        collection = getCollectionFromDb(passThroughCollectionName);
        LOG.info("{} is loaded into local storage.  Total Count = {}", passThroughCollectionName, collection.size());
    }
    
    /**
     * Transformation no-op.
     */
    private void transform() {
        LOG.info("No transformation for {}, passing straight through.", passThroughCollectionName);
    }
    
    /**
     * Persists the transformed data into staging mongo database.
     */
    private void persist() {
        LOG.info("Persisting transformed data into {}_transformed staging collection.", passThroughCollectionName);
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : collection.entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
            getNeutralRecordMongoAccess().getRecordRepository().createForJob(neutralRecord, getJob().getId());
        }
        LOG.info("Finished persisting transformed data into {}_transformed staging collection.",
                passThroughCollectionName);
    }
    
    /**
     * @return the passThroughCollectionName
     */
    public String getPassThroughCollectionName() {
        return passThroughCollectionName;
    }
    
    /**
     * @param passThroughCollectionName
     *            the passThroughCollectionName to set
     */
    public void setPassThroughCollectionName(String passThroughCollectionName) {
        this.passThroughCollectionName = passThroughCollectionName;
    }
}
