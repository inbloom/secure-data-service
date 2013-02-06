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

package org.slc.sli.ingestion.smooks;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.BatchJobStage;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.FileSource;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;

/**
 * Factory class for Smooks
 *
 * @author dduran
 *
 */
public class SliSmooksFactory implements BatchJobStage {

    private static final String STAGE_NAME = "Smooks Configuration";

    private Map<FileType, SliSmooksConfig> sliSmooksConfigMap;
    private String beanId;
    private NeutralRecordMongoAccess nrMongoStagingWriter;

    @Autowired
    private BatchJobDAO batchJobDAO;

    private DeterministicUUIDGeneratorStrategy dIdStrategy;
    private DeterministicIdResolver dIdResolver;

    private Set<String> recordLevelDeltaEnabledEntities;

    public SliSmooks createInstance(IngestionFileEntry ingestionFileEntry, AbstractMessageReport errorReport,
            ReportStats reportStats) throws IOException, SAXException {

        FileType fileType = ingestionFileEntry.getFileType();
        SliSmooksConfig sliSmooksConfig = sliSmooksConfigMap.get(fileType);
        if (sliSmooksConfig != null) {

            return createSmooksFromConfig(sliSmooksConfig, errorReport, reportStats,
                    ingestionFileEntry.getBatchJobId(), ingestionFileEntry);

        } else {
            errorReport.error(reportStats, new FileSource(ingestionFileEntry.getResourceId()), CoreMessageCode.CORE_0013, fileType);
            throw new IllegalArgumentException("File type not supported : " + fileType);
        }
    }

    private SliSmooks createSmooksFromConfig(SliSmooksConfig sliSmooksConfig, AbstractMessageReport errorReport,
            ReportStats reportStats, String batchJobId, IngestionFileEntry fe) throws IOException, SAXException {

        SliSmooks smooks = new SliSmooks(sliSmooksConfig.getConfigFileName());

        // based on target selectors for this file type, add visitors
        List<String> targetSelectorList = sliSmooksConfig.getTargetSelectors();
        if (targetSelectorList != null) {

            // just one visitor instance that can be added with multiple target selectors
            SmooksEdFiVisitor smooksEdFiVisitor = SmooksEdFiVisitor.createInstance(beanId, batchJobId, errorReport,
                    reportStats, fe);

            smooksEdFiVisitor.setNrMongoStagingWriter(nrMongoStagingWriter);
            smooksEdFiVisitor.setBatchJobDAO(batchJobDAO);
            smooksEdFiVisitor.setDIdGeneratorStrategy(dIdStrategy);
            smooksEdFiVisitor.setDIdResolver(dIdResolver);

            smooksEdFiVisitor.setRecordLevelDeltaEnabledEntities(recordLevelDeltaEnabledEntities);

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

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

}
