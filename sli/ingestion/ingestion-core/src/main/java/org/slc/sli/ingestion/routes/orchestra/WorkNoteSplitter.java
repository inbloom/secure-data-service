package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jms.IllegalStateException;

import org.apache.camel.Exchange;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.WorkNoteImpl;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * WorkNote splitter to be used from camel
 * 
 * @author dduran
 * 
 */
@Component
public class WorkNoteSplitter {
    
    private static final Logger LOG = LoggerFactory.getLogger(WorkNoteSplitter.class);
    
    private static final int ENTITY_SPLITTING_THRESHOLD = 25000;
    private static final int ENTITY_CONSTANT_SPLIT = 10000;
    
    @Autowired
    private StagedEntityTypeDAO stagedEntityTypeDAO;
    
    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;
    
    /**
     * Splits the work that can be processed in parallel next round into individual WorkNotes.
     * 
     * @param exchange
     * @return list of WorkNotes that camel will iterate over, issuing each as a new message
     * @throws IllegalStateException
     */
    public List<WorkNote> split(Exchange exchange) throws IllegalStateException {
        
        String jobId = exchange.getIn().getHeader("jobId").toString();
        LOG.info("orchestrating splitting for job: {}", jobId);
        
        Set<IngestionStagedEntity> stagedEntities = stagedEntityTypeDAO.getStagedEntitiesForJob(jobId);
        
        if (stagedEntities.size() == 0) {
            throw new IllegalStateException(
                    "stagedEntities is empty at splitting stage. should have been redirected prior to this point.");
        }
        
        Set<IngestionStagedEntity> nextTierEntities = IngestionStagedEntity.cleanse(stagedEntities);
        
        List<WorkNote> workNoteList = createWorkNotes(nextTierEntities, jobId);
        
        return workNoteList;
    }
    
    private List<WorkNote> createWorkNotes(Set<IngestionStagedEntity> stagedEntities, String jobId) {
        LOG.info("creating WorkNotes for processable entities: {}", stagedEntities);
        
        List<WorkNote> workNoteList = new ArrayList<WorkNote>();
        for (IngestionStagedEntity stagedEntity : stagedEntities) {
            long numRecords = neutralRecordMongoAccess.getRecordRepository().countForJob(
                    stagedEntity.getCollectionNameAsStaged(), new NeutralQuery(0), jobId);
            
            LOG.info("Found {} records for collection: {}", numRecords, stagedEntity.getCollectionNameAsStaged());
            
            if (numRecords > ENTITY_SPLITTING_THRESHOLD) {
                LOG.info("Splitting {} collection.", stagedEntity.getCollectionNameAsStaged());
                for (long i = 0; i < numRecords; i += ENTITY_CONSTANT_SPLIT) {
                    long chunk = ((i + ENTITY_CONSTANT_SPLIT) > numRecords) ? (numRecords)
                            : (i + ENTITY_CONSTANT_SPLIT);
                    WorkNote workNote = new WorkNoteImpl(jobId, stagedEntity, 0, chunk - 1);
                    workNoteList.add(workNote);
                }
            } else {
                WorkNote workNote = new WorkNoteImpl(jobId, stagedEntity, 0, numRecords - 1);
                workNoteList.add(workNote);
            }
        }
        return workNoteList;
    }
}
