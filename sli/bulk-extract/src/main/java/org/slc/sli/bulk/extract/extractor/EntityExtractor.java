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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.bulk.extract.files.DataExtractFile;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.treatment.TreatmentApplicator;
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

    private List<String> addToCollectionFile;

    private Repository<Entity> entityRepository;

    private Map<String, String> entitiesToCollections;

    private TreatmentApplicator applicator;

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

        Map<String, ArchiveEntry> archiveEntries = new HashMap<String, ArchiveEntry>();
        try {
            TenantContext.setTenantId(tenant);
            Iterator<Entity> cursor = entityRepository.findEach(collectionName, new Query());

            if (cursor.hasNext()) {
                LOG.info("Extracting from " + collectionName);

                while (cursor.hasNext()) {
                    Entity entity = cursor.next();

                    // Write entity and its embedded docs to their corresponding archives.
                    writeEntity(entity, collectionName, archiveEntries, archiveFile);
                }
            }
        } catch (Exception e) {
            LOG.error("Error while extracting from " + collectionName, e);
        } finally {
            TenantContext.setTenantId(null);
            closeArchiveEntries(archiveEntries);
        }
    }

    /**
     * Write entity and its embedded docs to their respective archives.
     * @param entity - entity to be written
     * @param collectionName - name of collection containing entity
     * @param archiveEntries - collection of archive entries, one per entity
     * @param archiveFile - file containing archives to be written to
     */
    private void writeEntity(Entity entity, String collectionName, Map<String, ArchiveEntry> archiveEntries, ExtractFile archiveFile) throws JsonGenerationException, JsonMappingException, IOException {
        // Write entity to its archive.
        if (entitiesToCollections.containsKey(entity.getType())) {
            if (!archiveEntries.containsKey(entity.getType())) {
                archiveEntries.put(entity.getType(), ArchiveEntry.createArchiveEntry(entity.getType(), archiveFile));
            }
            writeRecord(archiveEntries.get(entity.getType()), entity, false);
        }

        // Write entity to collection archive, if indicated.
        if (addToCollectionFile.contains(entity.getType())) {
            if (!archiveEntries.containsKey(collectionName)) {
                archiveEntries.put(collectionName, ArchiveEntry.createArchiveEntry(collectionName, archiveFile));
            }
            writeRecord(archiveEntries.get(collectionName), entity, true);
        }

        //Write entity subdocs.
        writeEmbeddedDocs(entity.getEmbeddedData(), archiveEntries, archiveFile);

        //Write entity container docs.
        writeEmbeddedDocs(entity.getContainerData(), archiveEntries, archiveFile);
    }

    /**
     * Write collection of an entity's embedded documents to their respective archives.
     * @param docs - embedded documents within an entity
     * @param archiveEntries - collection of archive entries, one per subdoc type
     * @param archiveFile - file containing archives to be written to
     */
    private void writeEmbeddedDocs(Map<String, List<Entity>> docs, Map<String, ArchiveEntry> archiveEntries, ExtractFile archiveFile) throws FileNotFoundException, IOException {
        for (String docName : docs.keySet()) {
            if (entitiesToCollections.containsKey(docName)) {
                if (!archiveEntries.containsKey(docName)) {
                    archiveEntries.put(docName, ArchiveEntry.createArchiveEntry(docName, archiveFile));
                }
                for (Entity doc : docs.get(docName)) {
                    writeRecord(archiveEntries.get(docName), doc, false);
                }
            }
        }
    }

    /**
     * Write record to archive entry.
     * @param archiveEntry - archive entry to which to be written
     * @param record - record to be written to archive entry
     * @param applyExtraTreatment - determines whether extra treatments should be applied
     */
    private void writeRecord(ArchiveEntry archiveEntry, Entity record, boolean applyExtraTreatment) throws JsonGenerationException, JsonMappingException, IOException {
        Entity treated = applicator.apply(record);
        if (applyExtraTreatment) {
            treated = applicator.applyExtra(treated);
        }
        archiveEntry.write(treated);
    }

    /**
     * Write record to archive entry.
     * @param archiveEntries - complete set of entity archive entries
     */
    private void closeArchiveEntries(Map<String, ArchiveEntry> archiveEntries) {
        for (String entity : archiveEntries.keySet()) {
            try {
                archiveEntries.get(entity).flush();
                archiveEntries.get(entity).close();

                LOG.info("Finished extracting {} records for " + entity,
                        archiveEntries.get(entity).getNoOfRecords());
           } catch (Exception e) {
               LOG.error("Error while closing archive for entity " + entity, e);
           }
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
     * Set entities to collections map.
<<<<<<< HEAD
     * @param entitiesToCollections - entities to collections map
=======
     * @param entitiesToCollections entities to collections map
>>>>>>> 7814f3a8afeb3962131c900dbb3c4a9b481c5a94
     */
    public void setEntitiesToCollections(Map<String, String> entitiesToCollections) {
        this.entitiesToCollections = entitiesToCollections;
    }

    /**
     * Set treatment applicator.
     * @param applicator - treatment applicator
     */
    public void setApplicator(TreatmentApplicator applicator) {
        this.applicator = applicator;
    }

    /**
     * Set add to collection file.
     * @param addToCollectionFile - collection of entities to add to collection archive
     */
    public void setAddToCollectionFile(List<String> addToCollectionFile) {
        this.addToCollectionFile = addToCollectionFile;
    }

    /**
     * Describes class to encapsulate archive file entry operations.
     */
     private static class ArchiveEntry {
        private static final JsonFactory JSON_FACTORY = new JsonFactory();
        private static final ObjectMapper MAPPER = new ObjectMapper();

        private long noOfRecords = 0;
        private DataExtractFile dataFile = null;
        private JsonGenerator jsonGenerator = null;

        protected ArchiveEntry(DataExtractFile dataFile) throws IOException {
            this.dataFile = dataFile;

            jsonGenerator = JSON_FACTORY.createJsonGenerator(dataFile.getOutputStream());
            jsonGenerator.setCodec(MAPPER);
            jsonGenerator.writeStartArray();
        }

        public static ArchiveEntry createArchiveEntry(String collectionName, ExtractFile extractFile) throws IOException {
            return new ArchiveEntry(extractFile.getDataFileEntry(collectionName));
        }

        public void write(Entity entity) throws JsonGenerationException, JsonMappingException, IOException {
            jsonGenerator.writeObject(entity.getBody());
            noOfRecords++;
        }

        public void flush() throws JsonGenerationException, IOException {
            jsonGenerator.writeEndArray();
            jsonGenerator.flush();
        }

        public void close() {
            try {
                jsonGenerator.close();
            } catch (IOException e) {
                // eat the exception
                e = null;
            }
            dataFile.close();
        }

        public long getNoOfRecords() {
            return noOfRecords;
        }
    };

}
