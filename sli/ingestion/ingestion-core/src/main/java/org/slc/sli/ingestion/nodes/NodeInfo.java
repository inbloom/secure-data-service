package org.slc.sli.ingestion.nodes;

import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Simple bean that provides information about this ingestion node.
 *
 * @author smelody
 *
 */
@Component
public class NodeInfo {

    private static final Logger LOG = LoggerFactory.getLogger(NodeInfo.class);

    private UUID uuid;

    @Value("${sli.ingestion.nodeType}")
    private String ingestionNodeType;

    public NodeInfo() {
        uuid = UUID.randomUUID();
    }

    @PostConstruct
    public void init() {

        LOG.info("Starting node with uuid: {} and node type: {}", uuid, ingestionNodeType);

    }

    /**
     * Returns the UUID of this node.
     *
     * @return
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns the type of ingestion node - loosely typed in IngestionNodeType.
     *
     * @return
     */
    public String getNodeType() {
        return ingestionNodeType;
    }

}
