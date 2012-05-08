package org.slc.sli.ingestion.routes.orchestra;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slc.sli.ingestion.EdfiEntity;

/**
 *
 * @author dduran
 *
 */
public class InMemoryStagedEntityTypeDAO implements StagedEntityTypeDAO {

    private static final Map<String, Set<EdfiEntity>> STAGED_ENTITIES_MAP = new HashMap<String, Set<EdfiEntity>>();

    @Override
    public Set<EdfiEntity> getStagedEntitiesForJob(String jobId) {

        Set<EdfiEntity> stagedEntities = STAGED_ENTITIES_MAP.get(jobId);

        if (stagedEntities == null) {
            stagedEntities = Collections.emptySet();
        }
        return stagedEntities;
    }

    @Override
    public void setStagedEntitiesForJob(Set<EdfiEntity> edfiEntities, String jobId) {
        STAGED_ENTITIES_MAP.put(jobId, edfiEntities);
    }

}
