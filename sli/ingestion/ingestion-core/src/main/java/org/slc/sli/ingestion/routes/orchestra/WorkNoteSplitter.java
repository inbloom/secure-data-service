package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jms.IllegalStateException;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.EdfiEntity;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.WorkNoteImpl;

/**
 * WorkNote splitter to be used from camel
 *
 * @author dduran
 *
 */
@Component
public class WorkNoteSplitter {

    private static final Logger LOG = LoggerFactory.getLogger(WorkNoteSplitter.class);

    @Autowired
    private StagedEntityTypeDAO stagedEntityTypeDAO;

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

        Set<EdfiEntity> stagedEntities = stagedEntityTypeDAO.getStagedEntitiesForJob(jobId);

        if (stagedEntities.size() == 0) {
            throw new IllegalStateException("stagedEntities is empty at splitting stage. should have been redirected prior to this point.");
        }

        Set<EdfiEntity> processableEntities = EdfiEntity.cleanse(stagedEntities);

        List<WorkNote> workNoteList = createWorkNotes(processableEntities, jobId);

        return workNoteList;
    }

    private List<WorkNote> createWorkNotes(Set<EdfiEntity> entities, String jobId) {
        LOG.info("creating WorkNotes for processable entities: {}", entities);

        List<WorkNote> workNoteList = new ArrayList<WorkNote>();
        for (EdfiEntity edfiEntity : entities) {

            WorkNote workNote = new WorkNoteImpl(jobId, edfiEntity.getEntityName(), 0, 0);
            workNoteList.add(workNote);
        }
        return workNoteList;
    }
}
