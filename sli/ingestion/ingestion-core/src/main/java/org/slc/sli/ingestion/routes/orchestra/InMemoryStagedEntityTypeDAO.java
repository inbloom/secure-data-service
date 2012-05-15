package org.slc.sli.ingestion.routes.orchestra;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slc.sli.ingestion.IngestionStagedEntity;

/**
 *
 * @author dduran
 *
 */
public class InMemoryStagedEntityTypeDAO implements StagedEntityTypeDAO {

    private static final Map<String, Set<IngestionStagedEntity>> STAGED_ENTITIES_MAP = new HashMap<String, Set<IngestionStagedEntity>>();

    /**
     * returns a copy of the set of staged entities for a given jobId
     */
    @Override
    public Set<IngestionStagedEntity> getStagedEntitiesForJob(String jobId) {

        Set<IngestionStagedEntity> stagedEntities = STAGED_ENTITIES_MAP.get(jobId);

        if (stagedEntities == null) {
            stagedEntities = Collections.emptySet();
        }
        return new HashSet<IngestionStagedEntity>(stagedEntities);
    }

    @Override
    public void setStagedEntitiesForJob(Set<IngestionStagedEntity> stagedEntities, String jobId) {
        STAGED_ENTITIES_MAP.put(jobId, stagedEntities);
    }

    /**
     * remove the given staged entity for the given jobId, if both exist.
     */
    @Override
    public synchronized boolean removeStagedEntityForJob(IngestionStagedEntity stagedEntity, String jobId) {
        Set<IngestionStagedEntity> stagedEntities = STAGED_ENTITIES_MAP.get(jobId);
        if (stagedEntities != null) {
            return stagedEntities.remove(stagedEntity);
        }
        return false;
    }

}
