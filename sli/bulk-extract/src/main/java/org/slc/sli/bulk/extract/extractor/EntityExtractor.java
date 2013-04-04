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
package org.slc.sli.bulk.extract.extractor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.files.writer.EntityWriter;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;


/**
 * Extractor pulls data for on entity from mongo and writes it to file.
 *
 * @author tshewchuk
 *
 */
public class EntityExtractor{

    private static final Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);

    private Repository<Entity> entityRepository;

    private Map<String, String> entitiesToCollections;

    private EntityWriter writer;

    /**
     * extract all the records of entity.
     *
     * @param tenant
     *          TenantId
     * @param archiveFile
     *          Archive File
     * @param collectionName
     *          Name of the entity to be extracted
     */
    public void extractEntities(String tenant, ExtractFile archiveFile, String collectionName) {
        try {
            TenantContext.setTenantId(tenant);
            Iterator<Entity> cursor = entityRepository.findEach(collectionName, new Query());

            if (cursor.hasNext()) {
                LOG.info("Extracting from " + collectionName);

                while (cursor.hasNext()) {
                    Entity entity = cursor.next();
                    if (entitiesToCollections.containsKey(entity.getType())) {
                        writer.write(entity, archiveFile);
                    }
                    //Write subdocs
                    extractAndWriteEmbeddedDocs(entity.getEmbeddedData(), archiveFile);

                    //Write container data
                    extractAndWriteEmbeddedDocs(entity.getContainerData(), archiveFile);
                }

                LOG.info("Finished extracting {} records for " + collectionName);
            }
        } catch (IOException e) {
            LOG.error("Error while extracting from " + collectionName, e);
        } finally {
            closeArchiveEntries(archiveFile);
        }
    }

    /**
     * Write collection of an entity's embedded documents to their respective archives.
     * @param docs - embedded documents within an entity
     * @param archiveEntries - collection of archive entries, one per subdoc type
     * @param archiveFile - file containing archives to be written to
     */
    private void extractAndWriteEmbeddedDocs(Map<String, List<Entity>> docs, ExtractFile archiveFile) throws FileNotFoundException, IOException {
        for (String docName : docs.keySet()) {
            if (entitiesToCollections.containsKey(docName)) {
                for (Entity doc : docs.get(docName)) {
                    writer.write(doc, archiveFile);
                }
            }
        }
    }

    /**
     * Write record to archive entry.
     * @param archiveEntries - complete set of entity archive entries
     */
    private void closeArchiveEntries(ExtractFile archiveFile) {
        for (String entity : entitiesToCollections.keySet()) {
            archiveFile.getDataFileEntry(entity).close();
        }
    }

    /**
     * set entity repository.
     * @param entityRepository - extractor entity repository
     */
    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

    /**
     * Set writer.
     * @param writer - writer the entity to a file
     */
    public void setWriter(EntityWriter writer) {
        this.writer = writer;
    }

    /**
     * Set entites to collection map.
     * @param entitiesToCollections entitiesToCollections
     */
    public void setEntitiesToCollections(Map<String, String> entitiesToCollections) {
        this.entitiesToCollections = entitiesToCollections;
    }
}

