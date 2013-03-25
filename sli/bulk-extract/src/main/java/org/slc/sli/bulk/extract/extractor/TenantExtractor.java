/*
* Copyright 2012 Shared Learning Collaborative, LLC
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

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.Launcher;
import org.slc.sli.bulk.extract.metadata.DataFile;
import org.slc.sli.bulk.extract.zip.OutstreamZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bulk extractor to extract data for a tenant
 *
 * @author tke
 *
 */
public class TenantExtractor{

    private static final Logger LOG = LoggerFactory.getLogger(TenantExtractor.class);

    private List<String> entities;

    private BulkExtractMongoDA bulkExtractMongoDA;

    private EntityExtractor entityExtractor;

    private DataFile metaData;

    public void execute(String tenant, OutstreamZipFile zipFile, Date startTime) {

        for (String entity : entities) {
            entityExtractor.extractEntity(tenant, zipFile, entity);
        }

        try {
            metaData.writeToZip(zipFile, Launcher.getTimeStamp(startTime));

            zipFile.closeZipFile();
            bulkExtractMongoDA.updateDBRecord(tenant, zipFile.getOutputFile().getAbsolutePath(), startTime);
        } catch (IOException e) {
            LOG.error("Error attempting to close zipfile " + zipFile.getOutputFile().getPath(), e);
        }
    }

    public BulkExtractMongoDA getBulkExtractMongoDA() {
        return bulkExtractMongoDA;
    }

    public void setBulkExtractMongoDA(BulkExtractMongoDA bulkExtractMongoDA) {
        this.bulkExtractMongoDA = bulkExtractMongoDA;
    }
    public void setEntities(List<String> entities) {
        this.entities = entities;
    }

    public DataFile getMetaData() {
        return metaData;
    }

    public void setMetaData(DataFile metaData) {
        this.metaData = metaData;
    }

    public EntityExtractor getEntityExtractor() {
        return entityExtractor;
    }

    public void setEntityExtractor(EntityExtractor entityExtractor) {
        this.entityExtractor = entityExtractor;
    }
}
