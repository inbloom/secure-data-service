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

package org.slc.sli.ingestion.transformation;

import java.io.IOException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.ingestion.EdfiEntity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * EdFi to SLI transformer based on Smooks
 *
 * @author okrook
 *
 */
@Component
public class SmooksEdFi2SLITransformer extends EdFi2SLITransformer {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(SmooksEdFi2SLITransformer.class);

    private final String EDFI_STUDENT_REFERENCE = "StudentReference";
    private final String EDFI_ASSESSMENT_REFERENCE = "AssessmentReference";
    private final String SLI_STUDENT_REFERENCE = "studentId";
    private final String SLI_ASSESSMENT_REFERENCE = "assessmentId";
    private final String EDFI_PROGRAM_REFERENCE = "ProgramReference";
    private final String SLC_PROGRAM_REFERENCE = "programReference";

    private Map<String, Smooks> smooksConfigs;

    @Override
    public List<SimpleEntity> transform(NeutralRecord item, ErrorReport errorReport) {
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

            if (entity.getMetaData() == null) {
                entity.setMetaData(new HashMap<String, Object>());
            }

            String externalId = (String) item.getLocalId();
            if (externalId != null) {
                entity.getMetaData().put("externalId", externalId);
            }

            if (EntityNames.STUDENT_ASSESSMENT_ASSOCIATION.equals(entity.getType())) {
                // Because the studentAssessmentAssociation goes through a Combiner
                // during the first Smooks translation. It would be quite complicated
                // to use Smooks mapping for the second Smooks translation.
                // All that needs doing is renaming the references from Ed-Fi names
                // to SLI names.
                Object ref = entity.getBody().remove(EDFI_STUDENT_REFERENCE);
                if (ref instanceof String) {
                    String studentId = (String) ref;
                    entity.getBody().put(SLI_STUDENT_REFERENCE, studentId);
                } else {
                    LOG.error("Unable to map '" + SLI_STUDENT_REFERENCE + "' in " + entity.getType() + ". Expected a String.");
                }

                ref = entity.getBody().remove(EDFI_ASSESSMENT_REFERENCE);
                if (ref instanceof String) {
                    String assessmentId = (String) ref;
                    entity.getBody().put(SLI_ASSESSMENT_REFERENCE, assessmentId);
                } else {
                    LOG.error("Unable to map 'assessmentId' in studentAssessmentAssociation. Expected a String.");
                }
            } else if (EntityNames.EDUCATION_ORGANIZATION.equals(entity.getType())
            		|| EdfiEntity.EDUCATION_SERVICE_CENTER.getEntityName().equals(entity.getType())
            		|| EdfiEntity.STATE_EDUCATION_AGENCY.getEntityName().equals(entity.getType())
            		|| EdfiEntity.LOCAL_EDUCATION_AGENCY.getEntityName().equals(entity.getType())
            		|| EdfiEntity.SCHOOL.getEntityName().equals(entity.getType())
            		)
            {
            	//This should catch EducationServiceCenter, StateEducationAgency, LocalEducationAgency, and School
                Object ref = entity.getBody().remove(EDFI_PROGRAM_REFERENCE);
                if (ref instanceof List<?>) {
                    List<?> references = (List<?>) ref;
                    entity.getBody().put(SLC_PROGRAM_REFERENCE, references);
                } else {
                    LOG.error("Unable to map '" + SLC_PROGRAM_REFERENCE + "' in " + entity.getType() + ". Expected a List.");
                }
            }

            return Arrays.asList(entity);
        }

        List<SimpleEntity> sliEntities;

        try {
            StringSource source = new StringSource(MAPPER.writeValueAsString(item));
            smooks.filterSource(source, result);
            sliEntities = getEntityListResult(result);
        } catch (IOException e) {
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

    @Override
    public List<List<SimpleEntity>> handle(List<NeutralRecord> items, ErrorReport errorReport) {
        // TODO Auto-generated method stub
        return null;
    }
}
