package org.slc.sli.ingestion.nodes;

import java.util.UUID;

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

    private UUID uuid;

    @Value("${sli.ingestion.nodeType}")
    private String ingestionNodeType;


    public NodeInfo() {
        uuid = UUID.randomUUID();
    }

    /**
     * Returns the UUID of this node.
     * @return
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns the type of ingestion node - loosely typed in IngestionNodeType.
     * @return
     */
    public String getNodeType() {
        return ingestionNodeType;
    }

}
