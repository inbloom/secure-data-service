/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
