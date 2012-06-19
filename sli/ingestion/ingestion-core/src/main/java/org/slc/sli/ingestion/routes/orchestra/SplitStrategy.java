package org.slc.sli.ingestion.routes.orchestra;

import java.util.List;

import org.slc.sli.ingestion.IngestionStagedEntity;
import org.slc.sli.ingestion.WorkNote;

/**
 * Strategy interface for splitting WorkNotes
 *
 * @author dduran
 *
 */
public interface SplitStrategy {

    /**
     * Splits out WorkNotes for the given IngestionStagedEntity and jobId
     *
     * @param stagedEntity
     * @param jobId
     * @return list of WorkNotes
     */
    List<WorkNote> splitForEntity(IngestionStagedEntity stagedEntity, String jobId);
}
