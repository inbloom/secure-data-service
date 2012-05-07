package org.slc.sli.ingestion.routes;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.WorkNoteImpl;

/**
 * @author ifaybyshev
 *
 */
@Component
public class EntityLevelSplitter {

    private static final Logger LOG = LoggerFactory.getLogger(EntityLevelSplitter.class);

    /**
     * The split message method returns something that is iteratable such as a java.util.List.
     *
     */
    public List<WorkNote> splitAndDistributeTopLevelEntities(Exchange exchange) {
        List<WorkNote> splitMessages = new ArrayList<WorkNote>();

        LOG.info("EXCHANGE = " + exchange.getIn().getHeader("BatchJobId", String.class));

        splitMessages.add(new WorkNoteImpl(exchange.getIn().getHeader("BatchJobId", String.class), "student", 0, 100));
        splitMessages.add(new WorkNoteImpl(exchange.getIn().getHeader("BatchJobId", String.class), "student", 101, 200));
        splitMessages.add(new WorkNoteImpl(exchange.getIn().getHeader("BatchJobId", String.class), "student", 201, 300));

        return splitMessages;
    }
}