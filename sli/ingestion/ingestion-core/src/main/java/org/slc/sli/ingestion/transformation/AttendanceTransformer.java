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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;


/**
 * Transforms disjoint set of attendance events into cleaner set of {school year : list of
 * attendance events} mappings and stores in the appropriate student-school or student-section
 * associations.
 *
 * @author shalka
 */
@Scope("prototype")
@Component("attendanceTransformationStrategy")
public class AttendanceTransformer extends AbstractTransformationStrategy implements MessageSourceAware {
    private static final Logger LOG = LoggerFactory.getLogger(AttendanceTransformer.class);

    private static final String ATTENDANCE = "attendance";
    //private static final String SCHOOL = "school";
    //private static final String SESSION = "session";
    //private static final String STUDENT_SCHOOL_ASSOCIATION = "studentSchoolAssociation";
    private static final String ATTENDANCE_TRANSFORMED = ATTENDANCE + "_transformed";

    //private int numAttendancesIngested = 0;

    private Map<Object, NeutralRecord> attendances;

    @SuppressWarnings("unused")
    private MessageSource messageSource;

    @Autowired
    @Qualifier("deterministicUUIDGeneratorStrategy")
    private UUIDGeneratorStrategy deterministicUUIDGeneratorStrategy;

    /**
     * Default constructor.
     */
    public AttendanceTransformer() {
        this.attendances = new HashMap<Object, NeutralRecord>();
    }
    
    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go."
     */
    @Override
    public void performTransformation() {
        this.loadData();
        this.transformAndPersist();
    }

    /**
     * Pre-requisite interchanges for daily attendance data to be successfully transformed:
     * student, education organization, education organization calendar, master schedule,
     * student enrollment
     */
    private void loadData() {
        LOG.info("Loading data for attendance transformation.");
        this.attendances = super.getCollectionFromDb(ATTENDANCE);
        LOG.info("{} is loaded into local storage.  Total Count = {}", ATTENDANCE, attendances.size());
    }

    /**
     * Transforms attendance events from Ed-Fi model into SLI model.
     */
    private void transformAndPersist() {
        LOG.info("Transforming attendance data");
        
        NeutralRecordRepository neutralRecordRepository = super.getNeutralRecordMongoAccess().getRecordRepository();
        
        Map<String, String> naturalKeys = new HashMap<String, String>();
        NaturalKeyDescriptor naturalKeyDescriptor = new NaturalKeyDescriptor(naturalKeys, null, "attendance", null);
        
        

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : attendances.entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            String studentId = (String) attributes.get("studentId");
            String schoolId = (String) attributes.get("schoolId");
            String schoolYear = (String) attributes.get("schoolYear");

            Map<String, Object> event = new HashMap<String, Object>();
            event.put("date", (String) attributes.get("eventDate"));
            event.put("event", (String) attributes.get("attendanceEventCategory"));
            if (attributes.containsKey("attendanceEventReason")) {
                event.put("reason", (String) attributes.get("attendanceEventReason"));
            }

            naturalKeys.put("studentId", studentId);
            naturalKeys.put("schoolId", schoolId);
            naturalKeys.put("schoolYear", schoolYear);
            
            String deterministicId = this.deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor);

            NeutralQuery query = new NeutralQuery(1);
            query.addCriteria(new NeutralCriteria(BATCH_JOB_ID_KEY, NeutralCriteria.OPERATOR_EQUAL, getBatchJobId(), false));
            query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, deterministicId, false));

            List<Map<String, Object>> attendanceEvent = new ArrayList<Map<String, Object>>();
            attendanceEvent.add (event);

            // need to use $each operator to add an array with $addToSet
            Object updateValue = attendanceEvent;
            if (attendanceEvent instanceof List) {
                Map<String, Object> eachList = new HashMap<String, Object>();
                eachList.put("$each", attendanceEvent);
                updateValue = eachList;
            }

            Map<String, Object> attendanceEventToPush = new HashMap<String, Object>();
            attendanceEventToPush.put("body.attendanceEvent", updateValue);

            Map<String, Object> update = new HashMap<String, Object>();
            update.put("addToSet", attendanceEventToPush);

            Object updatedExisting = neutralRecordRepository.updateMulti(query, update, ATTENDANCE_TRANSFORMED).getField("updatedExisting");
            
            // if did not update an existing document, an insert is required
            if (updatedExisting.equals(Boolean.FALSE)) {
                
                Map<String, Object> attendanceAttributes = new HashMap<String, Object>();
                attendanceAttributes.put("studentId", studentId);
                attendanceAttributes.put("schoolId", schoolId);
                attendanceAttributes.put("schoolYear", schoolYear);
                attendanceAttributes.put("attendanceEvent", attendanceEvent);
                
                NeutralRecord record = new NeutralRecord();
                record.setAttributes(attendanceAttributes);
                record.setBatchJobId(super.getBatchJobId());
                record.setRecordType(ATTENDANCE_TRANSFORMED);
                record.setSourceFile(this.attendances.values().iterator().next().getSourceFile());
                record.setLocationInSourceFile(attendances.values().iterator().next().getLocationInSourceFile());
                record.setCreationTime(super.getWorkNote().getRangeMinimum());
                record.setRecordId(deterministicId);

                super.insertRecord(record);
            }
        }

        LOG.info("Finished transforming attendance data");
    }


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
