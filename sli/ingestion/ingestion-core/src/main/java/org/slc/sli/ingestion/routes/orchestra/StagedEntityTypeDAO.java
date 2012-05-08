package org.slc.sli.ingestion.routes.orchestra;

import java.util.Set;

import org.slc.sli.ingestion.EdfiEntity;

/**
 *
 * @author dduran
 *
 */
public interface StagedEntityTypeDAO {

    Set<EdfiEntity> getStagedEntitiesForJob(String jobId);

    void setStagedEntitiesForJob(Set<EdfiEntity> edfiEntities, String jobId);

}
