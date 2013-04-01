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
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.bulk.extract.files.DataExtractFile;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.treatment.TreatmentApplicator;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.DBCollection;


/**
 * Extractor pulls data for on entity from mongo and writes it to file.
 *
 * @author tshewchuk
 *
 */
public class EntityExtractor{

    private static final Logger LOG = LoggerFactory.getLogger(EntityExtractor.class);

    private final List<String> entities = new ArrayList<String>();

    private List<String> excludedCollections;

    private List<String> yearlyTranscriptSubdocs;

    private Repository<Entity> entityRepository;

    private TreatmentApplicator applicator;

    private class ArchiveEntry {
        private long noOfRecords = 0;
        private DataExtractFile dataFile = null;
        private OutputStream outputStream = null;
        private final JsonFactory jsonFactory = new JsonFactory();
        private JsonGenerator jsonGenerator = null;
        private final ObjectMapper mapper = new ObjectMapper();

        public ArchiveEntry(String collectionName, ExtractFile archiveFile) throws FileNotFoundException, IOException {
            dataFile = archiveFile.getDataFileEntry(collectionName);
            outputStream = dataFile.getOutputStream();
            jsonGenerator = jsonFactory.createJsonGenerator(outputStream);
            jsonGenerator.writeStartArray();
        }

        public void incrementNoOfRecords() {
            noOfRecords++;
        }

        public void writeValue(Entity entity) throws JsonGenerationException, JsonMappingException, IOException {
            mapper.writeValue(jsonGenerator, entity.getBody());
        }

        public void flush() throws JsonGenerationException, IOException {
            jsonGenerator.writeEndArray();
            jsonGenerator.flush();
        }

        public void closeDatafile() {
            if (dataFile != null) {
                dataFile.close();
            }
        }

        public long getNoOfRecords() {
            return noOfRecords;
        }
    };

    public EntityExtractor() throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = EntityNames.class.getFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                entities.add(field.get(null).toString());
            }
        }
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
            //            DBCursor cursor = entityRepository.getCollection(collectionName).find(query.getQueryObject());
            Iterator<Entity> cursor = entityRepository.findEach(collectionName, query);

            if (cursor.hasNext()) {
                LOG.info("Extracting from " + collectionName);

                while (cursor.hasNext()) {
                    //                    DBObject record = cursor.next();
                    Entity entity = cursor.next();

                    // Write entity to archive.
                    //                    Entity entity = MongoEntity.fromDBObject(record);
                    if (entities.contains(entity.getType())) {
                        if (!archiveEntries.containsKey(entity.getType())) {
                            archiveEntries.put(entity.getType(), new ArchiveEntry(entity.getType(), archiveFile));
                        }
                        writeRecord(archiveEntries.get(entity.getType()), entity);
                    } else if (entities.contains(collectionName)) {  // Remove condition to include all collections.
                        if (!archiveEntries.containsKey(collectionName)) {
                            archiveEntries.put(collectionName, new ArchiveEntry(collectionName, archiveFile));
                        }
                        writeRecord(archiveEntries.get(collectionName), entity);
                    }

                    // Write subdocs to archive.
                    Map<String, List<Entity>> subdocs = entity.getEmbeddedData();
                    for (String subdocName : subdocs.keySet()) {
                        if (entities.contains(subdocName)) {
                            if (!archiveEntries.containsKey(subdocName)) {
                                archiveEntries.put(subdocName, new ArchiveEntry(subdocName, archiveFile));
                            }
                            for (Entity subdoc : subdocs.get(subdocName)) {
                                writeRecord(archiveEntries.get(subdocName), subdoc);
                            }
                        }
                    }

                    // Write container docs to archive.
                    Map<String, List<Entity>> containedDocs = entity.getContainerData();
                    for (String containedDocName : containedDocs.keySet()) {
                        if (entities.contains(containedDocName)) {
                            if (!archiveEntries.containsKey(containedDocName)) {
                                archiveEntries.put(containedDocName, new ArchiveEntry(containedDocName, archiveFile));
                            }
                            for (Entity containedDoc : containedDocs.get(containedDocName)) {
                                writeRecord(archiveEntries.get(containedDocName), containedDoc);
                            }
                        }
                    }
                }

                for (String entity : archiveEntries.keySet()) {
                    archiveEntries.get(entity).flush();
                    LOG.info("Finished extracting {} records for " + entity,
                            archiveEntries.get(entity).getNoOfRecords());
                }
            }

        } catch (IOException e) {
            LOG.error("Error while extracting from " + collectionName, e);
        } finally {
            TenantContext.setTenantId(null);
            for (String entity : archiveEntries.keySet()) {
                archiveEntries.get(entity).closeDatafile();
            }
        }
    }

    /**
     * Write record to archive entry.
     * @param archive entry
     * @param record
     */
    private void writeRecord(ArchiveEntry archiveEntry, Entity record) throws JsonGenerationException, JsonMappingException, IOException {
        Entity treated = applicator.apply(record);
        archiveEntry.writeValue(treated);
        archiveEntry.incrementNoOfRecords();
    }

    /**
     * set excluded collections.
     * @param excluded collections
     */
    public void setExcludedCollections(List<String> excludedCollections) {
        this.excludedCollections = excludedCollections;
    }

    /**
     * set excluded collections.
     * @param excluded collections
     */
    public void setYearlyTranscriptSubdocs(List<String> yearlyTranscriptSubdocs) {
        this.yearlyTranscriptSubdocs = yearlyTranscriptSubdocs;
    }

    /**
     * set entity repository.
     * @param entityRepository entity repository
     */
    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

    /**
     * get applicator.
     * @return treatment applicator
     */
    public TreatmentApplicator getApplicator() {
        return applicator;
    }

    /**
     * set applicator.
     * @param applicator applicator
     */
    public void setApplicator(TreatmentApplicator applicator) {
        this.applicator = applicator;
    }

    /**
     * Get collection names for a tenant.
     * @param tenant tenant
     * @return collection names
     */
    public List<String> getCollectionNames(String tenant) {
        TenantContext.setTenantId(tenant);
        List<DBCollection> collections = entityRepository.getCollections(false);
        List<String> collectionNames = new ArrayList<String>();
        for (DBCollection collection : collections) {
            if (!excludedCollections.contains(collection.getName())) {
                collectionNames.add(collection.getName());
            }
        }
        TenantContext.setTenantId(null);
        return collectionNames;
    }

}
