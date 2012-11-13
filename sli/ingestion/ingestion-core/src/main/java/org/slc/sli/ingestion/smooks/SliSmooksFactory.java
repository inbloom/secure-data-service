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

package org.slc.sli.ingestion.smooks;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.milyn.Smooks;
import org.milyn.delivery.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.validation.ErrorReport;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;

import scala.actors.threadpool.Arrays;

/**
 * Factory class for Smooks
 *
 * @author dduran
 *
 */
public class SliSmooksFactory {

    private Map<FileType, SliSmooksConfig> sliSmooksConfigMap;
    private String beanId;
    private NeutralRecordMongoAccess nrMongoStagingWriter;

    @Autowired
    public BatchJobDAO batchJobDAO;

    @Autowired
    public DeterministicUUIDGeneratorStrategy dIdStrategy;

    @Autowired
    public DeterministicIdResolver dIdResolver;

    private Set<String> recordLevelDeltaEnabledEntities;

    public Smooks createInstance(IngestionFileEntry ingestionFileEntry, ErrorReport errorReport) throws IOException, SAXException {

        FileType fileType = ingestionFileEntry.getFileType();
        SliSmooksConfig sliSmooksConfig = sliSmooksConfigMap.get(fileType);
        if (sliSmooksConfig != null) {

            return createSmooksFromConfig(sliSmooksConfig, errorReport, ingestionFileEntry.getBatchJobId(),
                    ingestionFileEntry);

        } else {
            errorReport.fatal("File type not supported : " + fileType, SliSmooksFactory.class);
            throw new IllegalArgumentException("File type not supported : " + fileType);
        }
    }

    private Smooks createSmooksFromConfig(SliSmooksConfig sliSmooksConfig, ErrorReport errorReport, String batchJobId,
            IngestionFileEntry fe) throws IOException, SAXException {

        Smooks smooks = new Smooks(sliSmooksConfig.getConfigFileName());

        // based on target selectors for this file type, add visitors
        List<String> targetSelectorList = sliSmooksConfig.getTargetSelectors();
        if (targetSelectorList != null) {

            // just one visitor instance that can be added with multiple target selectors
            Visitor smooksEdFiVisitor = SmooksEdFiVisitor.createInstance(beanId, batchJobId, errorReport, fe);

            ((SmooksEdFiVisitor) smooksEdFiVisitor).setNrMongoStagingWriter(nrMongoStagingWriter);
            ((SmooksEdFiVisitor) smooksEdFiVisitor).setBatchJobDAO(batchJobDAO);
            ((SmooksEdFiVisitor) smooksEdFiVisitor).setDidGeneratorStrategy(dIdStrategy);
            ((SmooksEdFiVisitor) smooksEdFiVisitor).setDidResolver(dIdResolver);

            ((SmooksEdFiVisitor) smooksEdFiVisitor).setRecordLevelDeltaEnabledEntities(recordLevelDeltaEnabledEntities);

            for (String targetSelector : targetSelectorList) {
                smooks.addVisitor(smooksEdFiVisitor, targetSelector);
            }
        }
        return smooks;
    }

    public void setRecordLevelDeltaEnabledEntities(Set<String> recordLevelDeltaEnabledEntities) {
        this.recordLevelDeltaEnabledEntities = recordLevelDeltaEnabledEntities;
    }

    public void setSliSmooksConfigMap(Map<FileType, SliSmooksConfig> sliSmooksConfigMap) {
        this.sliSmooksConfigMap = sliSmooksConfigMap;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public void setNrMongoStagingWriter(ResourceWriter<NeutralRecord> nrMongoStagingWriter) {
        this.nrMongoStagingWriter = (NeutralRecordMongoAccess) nrMongoStagingWriter;
    }

    public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
        this.batchJobDAO = batchJobDAO;
    }

    public void setdIdStrategy(DeterministicUUIDGeneratorStrategy dIdStrategy) {
        this.dIdStrategy = dIdStrategy;
    }

    public void setdIdResolver(DeterministicIdResolver dIdResolver) {
        this.dIdResolver = dIdResolver;
    }

}
