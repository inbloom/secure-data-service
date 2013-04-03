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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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

    private List<String> entities;

    @PostConstruct
    public void init() {
        entities = new ArrayList<String>(new HashSet<String>(entitiesToCollections.keySet()));
    }

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
        Query query = new Query();
        try {
            TenantContext.setTenantId(tenant);
            Iterator<Entity> cursor = entityRepository.findEach(collectionName, query);

            if (cursor.hasNext()) {
                LOG.info("Extracting from " + collectionName);

                while (cursor.hasNext()) {
                    Entity entity = cursor.next();

                    // Write entity to archive.
                    writeEntityToArchive(entity, collectionName, archiveEntries, archiveFile);

                    //Write subdocs
                    writeEmbeddedDocs(entity.getEmbeddedData(), archiveEntries, archiveFile);

                    //Write container data
                    writeEmbeddedDocs(entity.getContainerData(), archiveEntries, archiveFile);
                }

                cleanupArchiveFiles(archiveEntries);
            }

        } catch (IOException e) {
            LOG.error("Error while extracting from " + collectionName, e);
        } finally {
            TenantContext.setTenantId(null);
            for (String entity : archiveEntries.keySet()) {
                archiveEntries.get(entity).close();
            }
        }
    }

    private void writeEntityToArchive(Entity entity, String collectionName, Map<String, ArchiveEntry> archiveEntries, ExtractFile archiveFile) throws JsonGenerationException, JsonMappingException, IOException {
        if (entities.contains(entity.getType())) {
            if (!archiveEntries.containsKey(entity.getType())) {
                archiveEntries.put(entity.getType(), ArchiveEntry.createArchiveEntry(entity.getType(), archiveFile));
            }
            writeRecord(archiveEntries.get(entity.getType()), entity, false);
        }

        if (addToCollectionFile.contains(entity.getType())) {
            if (!archiveEntries.containsKey(collectionName)) {
                archiveEntries.put(collectionName, ArchiveEntry.createArchiveEntry(collectionName, archiveFile));
            }

            writeRecord(archiveEntries.get(collectionName), entity, true);
        }
    }

    private void writeEmbeddedDocs(Map<String, List<Entity>> docs, Map<String, ArchiveEntry> archiveEntries, ExtractFile archiveFile) throws FileNotFoundException, IOException {
        for (String docName : docs.keySet()) {
            if (entities.contains(docName)) {
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
     * @param archive entry
     * @param record
     */
    private void writeRecord(ArchiveEntry archiveEntry, Entity record, boolean applyExtraTreatment) throws JsonGenerationException, JsonMappingException, IOException {
        Entity treated = applicator.apply(record);
        if (applyExtraTreatment) {
            treated = applicator.applyExtra(treated);
        }
        archiveEntry.write(treated);
    }

    private void cleanupArchiveFiles(Map<String, ArchiveEntry> archiveEntries) throws JsonGenerationException, IOException {
        for (String entity : archiveEntries.keySet()) {
            archiveEntries.get(entity).flush();
            LOG.info("Finished extracting {} records for " + entity,
                    archiveEntries.get(entity).getNoOfRecords());
        }
    }

    /**
     * Set list of entities to extract.
     * @param entities entities
     */
    public void setEntities(List<String> entities) {
        this.entities = entities;
    }

    /**
     * set entity repository.
     * @param entityRepository entity repository
     */
    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

    /**
     * Set entities to collections map.
     * @param entitiesToCollections entities to collections map
     */
    public void setEntitiesToCollections(Map<String, String> entitiesToCollections) {
        this.entitiesToCollections = entitiesToCollections;
    }

    /**
     * set applicator.
     * @param applicator applicator
     */
    public void setApplicator(TreatmentApplicator applicator) {
        this.applicator = applicator;
    }

    /**
     * get list of entities which should also be added to their collection extract file.
     * @return addToCollectionFile
     */
    public List<String> getAddToCollectionFile() {
        return addToCollectionFile;
    }

    /**
     * set addToCollectionFile.
     * @param addToCollectionFile addToCollectionFile
     */
    public void setAddToCollectionFile(List<String> addToCollectionFile) {
        this.addToCollectionFile = addToCollectionFile;
    }

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
