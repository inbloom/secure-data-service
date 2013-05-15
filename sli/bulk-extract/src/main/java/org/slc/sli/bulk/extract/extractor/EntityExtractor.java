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
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slc.sli.bulk.extract.files.EntityWriterManager;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;


/**
 * Extractor pulls data for on entity from mongo and writes it to file.
 *
 * @author tshewchuk
 *
 */
public class EntityExtractor{

    private static final Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);

    private Repository<Entity> entityRepository;

    private EntityWriterManager writer;

    private NeutralQuery extractionQuery;

    /**
     * extract all the records of entity.
     *
     * @param archiveFile
     *          Archive File
     * @param collectionName
     *          Name of the entity to be extracted
     */
    public void extractEntities(ExtractFile archiveFile, String collectionName) {
        try {
            if (extractionQuery == null) {
                extractionQuery = new NeutralQuery();
            }
            Iterator<Entity> cursor = entityRepository.findEach(collectionName, extractionQuery);
            if (cursor.hasNext()) {
                LOG.info("Extracting from " + collectionName);
                CollectionWrittenRecord collectionRecord = new CollectionWrittenRecord(collectionName);

                while (cursor.hasNext()) {
                    Entity entity = cursor.next();

                    write(entity, archiveFile, collectionRecord, null);

                }

                LOG.info("Finished extracting " + collectionRecord.toString());
            }
        } catch (IOException e) {
            LOG.error("Error while extracting from " + collectionName, e);
        }
    }
    
    /**
     * Writes a single entity to an extract file.
     * 
     * @param archiveFile
     * @param collectionName
     */
    public void extractEntity(Entity entity, ExtractFile archiveFile, String collectionName, Predicate<Entity> filter) {
        try {
            write(entity, archiveFile, new CollectionWrittenRecord(collectionName), filter);
        } catch (IOException e) {
            LOG.error("Error while extracting from " + collectionName, e);
        }
    }
    
    public void extractEntity(Entity entity, ExtractFile archiveFile, String collectionName) {
        if(archiveFile!=null){
            extractEntity(entity, archiveFile, collectionName, null);
        }
    }

	/**
     * Writes an entity to a file.
     * @param entity entity
     * @param archiveFile archiveFile
     * @param collectionRecord collectionRecord
     * @throws FileNotFoundException FileNotFoundException
     * @throws IOException IOException
     */
    public void write(Entity entity, ExtractFile archiveFile, CollectionWrittenRecord collectionRecord, Predicate<Entity> filter)
            throws FileNotFoundException, IOException {
        writer.write(entity, archiveFile);
        collectionRecord.incrementNumberOfEntitiesWritten();
        //Write subdocs
        writeEmbeddedDocs(entity.getEmbeddedData(), archiveFile, collectionRecord, filter);

        //Write container data
        writeEmbeddedDocs(entity.getContainerData(), archiveFile, collectionRecord, filter);
    }


    /**
     * Write collection of an entity's embedded documents to their respective archives.
     * @param docs - embedded documents within an entity
     * @param archiveEntries - collection of archive entries, one per subdoc type
     * @param archiveFile - file containing archives to be written to
     * @param collectionRecord collectionRecord
     */
    private void writeEmbeddedDocs(Map<String, List<Entity>> docs, ExtractFile archiveFile,
            CollectionWrittenRecord collectionRecord, Predicate<Entity> filter) throws FileNotFoundException, IOException {
        for (String docName : docs.keySet()) {
                for (Entity doc : docs.get(docName)) {
                    if (doc != null && (filter == null || filter.apply(doc))) {
                        writer.write(doc, archiveFile);
                    } else {
                        LOG.warn("Embedded Doc {} has null value", docName);
                    }
                }
                collectionRecord.addEmbeddedDocWrittenRecord(docName, docs.get(docName).size());
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
     * Sets an optional query to be used in the extraction.
     *
     * @param extractionQuery
     */
    public void setExtractionQuery(NeutralQuery extractionQuery) {
        this.extractionQuery = extractionQuery;
    }

    /**
     * Set writer.
     * @param writer - writer the entity to a file
     */
    public void setWriter(EntityWriterManager writer) {
        this.writer = writer;
    }

    /**
     * A helper class to record number of writtens entities, including embedded docs
     *
     * @author slee
     *
     */
    public static class CollectionWrittenRecord {
        final String collectionName;
        long numberOfEntitiesWritten;
        Map<String, Long> embeddedDocWrittenRecords = new HashMap<String, Long>();

        CollectionWrittenRecord(String name) {
            this.collectionName = name;
        }

        @SuppressWarnings("boxing")
        void addEmbeddedDocWrittenRecord(String docName, long records) {
            long total = records;
            if (embeddedDocWrittenRecords.containsKey(docName)) {
                total = records + embeddedDocWrittenRecords.get(docName);
            }
            embeddedDocWrittenRecords.put(docName, total);
        }

        void incrementNumberOfEntitiesWritten() {
            ++numberOfEntitiesWritten;
        }

        @Override
        public String toString() {
            Object[] collArguments = { collectionName, new Long(numberOfEntitiesWritten)};
            StringBuffer sb = new StringBuffer(MessageFormat.format("{1,number,#} records for {0}", collArguments));

            for (Map.Entry<String, Long> entry : embeddedDocWrittenRecords.entrySet()) {
                Object[] embeddedArguments = { entry.getKey(), entry.getValue()};
                sb.append(MessageFormat.format("\n\t{1,number,#} embedded records for {0}", embeddedArguments));
            }

            return sb.toString();
        }
    }

}

