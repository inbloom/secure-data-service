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
package org.slc.sli.search.process.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slc.sli.search.entity.IndexEntity;
import org.slc.sli.search.entity.IndexEntity.Action;
import org.slc.sli.search.process.IncrementalLoader;
import org.slc.sli.search.process.Indexer;
import org.slc.sli.search.transform.IndexEntityConverter;
import org.slc.sli.search.util.OplogConverter;
import org.slc.sli.search.util.OplogConverter.Meta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Listens to ActiveMQ for Sarje messages. Filters the message and passes
 * data to the Indexer to be indexed in elasticsearch.
 *
 * @author dwu
 *
 */
public class IncrementalListenerImpl implements IncrementalLoader {

    private static final Logger LOG = LoggerFactory.getLogger(IncrementalListenerImpl.class);

    private Indexer indexer;

    private IndexEntityConverter indexEntityConverter;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Process a message from the queue
     *
     * @param opLog
     */
    @Override
    public void process(String message) {
        if (message != null) {
            try {
                processEntities(message);
            } catch (Exception e) {
                LOG.error("Error processing message", e);
            }
        }
    }

    /**
     * Convert oplog message to an IndexEntity, based on the action type (insert, update, delete)
     *
     * @param opLog
     * @return index entities
     * @throws Exception
     */
    public List<IndexEntity> processEntities(String opLogString) throws Exception {

        List<IndexEntity> entities = new LinkedList<IndexEntity>();

        List<Map<String, Object>> opLogs = mapper.readValue(OplogConverter.preProcess(opLogString),
                new TypeReference<List<Map<String, Object>>>() {
                });
        List<IndexEntity> indexEntities;
        // check action type and convert to an index entity
        for (Map<String, Object> opLog : opLogs) {
            try {
                indexEntities = convertToEntity(opLog);
                if (indexEntities!= null) {
                	for (IndexEntity ie : indexEntities) {
                		entities.add(ie);
                		index(ie);
                	}
                }
            } catch (Exception e) {
                LOG.info("Unable to process an oplog entry, skipping");
            }
        }
        return entities;
    }

    private List<IndexEntity> convertToEntity(Map<String, Object> opLogMap) throws Exception {
        Action action = OplogConverter.getAction(opLogMap);
        Meta meta = OplogConverter.getMeta(opLogMap);
        Map<String, Object> entity = OplogConverter.getEntity(action, opLogMap);
        if (action == Action.INDEX) {
            action = Action.UPDATE; 
        }
        return (entity == null) ? null : indexEntityConverter.fromEntity(meta.getIndex(), action, entity);
    }

    protected void index(IndexEntity ie) {
        indexer.index(ie);
    }


    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }


    public void setIndexEntityConverter(IndexEntityConverter indexEntityConverter) {
        this.indexEntityConverter = indexEntityConverter;
    }


    @Override
    public String getHealth() {
        return getClass() + "{}";
    }

}
