package org.slc.sli.ingestion.routes.orchestra;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * Latching mechanism to synchronize WorkNote processing program flow
 *
 * @author dduran
 *
 */
@Component
public class WorkNoteLatch {

    @Autowired
    BatchJobDAO batchJobDAO;

    @Handler
    public void receive(Exchange exchange) throws Exception {

        String messageType = exchange.getIn().getHeader("IngestionMessageType").toString();

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        String recordType = workNote.getIngestionStagedEntity().getCollectionNameAsStaged();
        if (MessageType.PERSIST_REQUEST.name().equals(messageType)) {
            // persist latch is cross-entity
            recordType = null;
        }

        if (batchJobDAO.countDownWorkNoteLatch(messageType, workNote.getBatchJobId(), recordType)) {
            exchange.getIn().setHeader("latchOpened", true);
        }
    }
}
