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

package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;
import org.milyn.payload.StringSource;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;

/**
 * EdFi to SLI transformer based on Smooks
 *
 * @author okrook
 *
 */
@Component
public class SmooksEdFi2SLITransformer extends EdFi2SLITransformer {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String STAGE_NAME = "Smooks EdFi To SLI Transformer";

    private Map<String, Smooks> smooksConfigs;

    @Override
    public List<SimpleEntity> transform(NeutralRecord item, AbstractMessageReport report, ReportStats reportStats) {
        JavaResult result = new JavaResult();
        Smooks smooks = smooksConfigs.get(item.getRecordType());

        // if no smooks configured for this, then just convert the neutral record to a SimpleEntity
        // directly.
        if (smooks == null) {
            String type = item.getRecordType().replaceAll("_transformed", "");
            Map<String, Object> body = item.getAttributes();
            SimpleEntity entity = new SimpleEntity();
            entity.setType(type);
            entity.setBody(body);
            entity.setStagedEntityId(item.getRecordId());

            Integer recordNumber = item.getLocationInSourceFile();
            if (recordNumber != null) {
                entity.setRecordNumber(recordNumber.longValue());
            }
            entity.setSourceFile(item.getSourceFile());

            entity.setVisitBeforeLineNumber(item.getVisitBeforeLineNumber());
            entity.setVisitBeforeColumnNumber(item.getVisitBeforeColumnNumber());
            entity.setVisitAfterLineNumber(item.getVisitAfterLineNumber());
            entity.setVisitAfterColumnNumber(item.getVisitAfterColumnNumber());

            if (entity.getMetaData() == null) {
                entity.setMetaData(new HashMap<String, Object>());
            }

            String externalId = (String) item.getLocalId();
            if (externalId != null) {
                entity.getMetaData().put("externalId", externalId);
            }

            return Arrays.asList(entity);
        }

        List<SimpleEntity> sliEntities;

        try {
            StringSource source = new StringSource(MAPPER.writeValueAsString(item));
            smooks.filterSource(source, result);
            sliEntities = getEntityListResult(result);
            for (SimpleEntity entity : sliEntities) {
                entity.setVisitBeforeLineNumber(item.getVisitBeforeLineNumber());
                entity.setVisitBeforeColumnNumber(item.getVisitBeforeColumnNumber());
                entity.setVisitAfterLineNumber(item.getVisitAfterLineNumber());
                entity.setVisitAfterColumnNumber(item.getVisitAfterColumnNumber());
            }
        } catch (java.io.IOException e) {
            sliEntities = Collections.emptyList();
        }

        return sliEntities;
    }

    /**
     * Traverse the results map to get the entity list
     */
    @SuppressWarnings("unchecked")
    private List<SimpleEntity> getEntityListResult(JavaResult result) {
        List<SimpleEntity> entityList = new ArrayList<SimpleEntity>();
        for (Entry<String, Object> resEntry : result.getResultMap().entrySet()) {
            if (resEntry.getValue() instanceof List) {
                List<?> list = (List<?>) resEntry.getValue();
                if (list.size() != 0 && list.get(0) instanceof SimpleEntity) {
                    entityList = (List<SimpleEntity>) list;
                    break;
                }
            }
        }
        return entityList;
    }

    public Map<String, Smooks> getSmooksConfigs() {
        return smooksConfigs;
    }

    public void setSmooksConfigs(Map<String, Smooks> smooksConfigs) {
        this.smooksConfigs = smooksConfigs;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.ingestion.handler.Handler#handle(java.util.List,
     * org.slc.sli.ingestion.reporting.AbstractMessageReport,
     * org.slc.sli.ingestion.reporting.ReportStats)
     */
    @Override
    public List<List<SimpleEntity>> handle(List<NeutralRecord> items, AbstractMessageReport report,
            ReportStats reportStats) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }
}
