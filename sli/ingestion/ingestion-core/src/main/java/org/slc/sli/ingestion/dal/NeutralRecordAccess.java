package org.slc.sli.ingestion.dal;

import org.slc.sli.ingestion.IngestionStagedEntity;

/**
 * Interface for access to NeutralRecords
 *
 * @author dduran
 *
 */
public interface NeutralRecordAccess {

    long collectionCountForJob(String collectionNameAsStaged, String jobId);

    long countCreationTimeWithinRange(String collectionName, long min, long max, String jobId);

    long getMaxCreationTimeForEntity(IngestionStagedEntity stagedEntity, String jobId);

    long getMinCreationTimeForEntity(IngestionStagedEntity stagedEntity, String jobId);

}
