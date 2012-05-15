package org.slc.sli.ingestion.routes.orchestra;

import java.util.Set;

import org.slc.sli.ingestion.IngestionStagedEntity;

/**
 *
 * @author dduran
 *
 */
public interface StagedEntityTypeDAO {

    Set<IngestionStagedEntity> getStagedEntitiesForJob(String jobId);

    boolean removeStagedEntityForJob(IngestionStagedEntity stagedEntity, String jobId);

    void setStagedEntitiesForJob(Set<IngestionStagedEntity> stagedEntities, String jobId);

}
