package org.slc.sli.ingestion.routes.orchestra;

import java.util.Collections;
import java.util.HashMap;
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

    @Override
    public Set<IngestionStagedEntity> getStagedEntitiesForJob(String jobId) {

        Set<IngestionStagedEntity> stagedEntities = STAGED_ENTITIES_MAP.get(jobId);

        if (stagedEntities == null) {
            stagedEntities = Collections.emptySet();
        }
        return stagedEntities;
    }

    @Override
    public void setStagedEntitiesForJob(Set<IngestionStagedEntity> stagedEntities, String jobId) {
        STAGED_ENTITIES_MAP.put(jobId, stagedEntities);
    }

}
