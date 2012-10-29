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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;

import com.mongodb.WriteResult;
/**
 * Transforms disjoint set of attendance events into cleaner set of {school year : list of
 * attendance events} mappings and stores in the appropriate student-school or student-section
 * associations.
 *
 * @author shalka
 */
@Scope("prototype")
@Component("studentAttendanceTransformationStrategy")
public class StudentAttendanceTransformer extends AbstractTransformationStrategy implements MessageSourceAware{
    private static final Logger LOG = LoggerFactory.getLogger(StudentAttendanceTransformer.class);

    private static final String ATTENDANCE = "studentAttendance";
    private static final String SCHOOL = "school";
    private static final String SESSION = "session";
    private static final String STUDENT_SCHOOL_ASSOCIATION = "studentSchoolAssociation";
    private static final String ATTENDANCE_TRANSFORMED = ATTENDANCE + "_transformed";

    private int numAttendancesIngested = 0;

    private Map<Object, NeutralRecord> attendances;

    private MessageSource messageSource;

    @Autowired
    private UUIDGeneratorStrategy type1UUIDGeneratorStrategy;

    /**
     * Default constructor.
     */
    public StudentAttendanceTransformer() {
        attendances = new HashMap<Object, NeutralRecord>();
    }

    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go."
     */
    @Override
    public void performTransformation() {
        loadData();
        transformAndPersist();
    }

    /**
     * Pre-requisite interchanges for daily attendance data to be successfully transformed:
     * student, education organization, education organization calendar, master schedule,
     * student enrollment
     */
    public void loadData() {
        LOG.info("Loading data for attendance transformation.");
        attendances = getCollectionFromDb(ATTENDANCE);
        LOG.info("{} is loaded into local storage.  Total Count = {}", ATTENDANCE, attendances.size());
    }

    /**
     * Transforms attendance events from Ed-Fi model into SLI model.
     */
    public void transformAndPersist() {
        LOG.info("Transforming attendance data");

        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : attendances.entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            String studentId = (String) attributes.get("studentId");
            String schoolId = (String) attributes.get("schoolId");
            String schoolYear = (String) attributes.get("schoolYear");

            Map<String, Object> event = new HashMap<String, Object>();
            String eventDate = (String) attributes.get("eventDate");
            String eventCategory = (String) attributes.get("attendanceEventCategory");
            event.put("date", eventDate);
            event.put("event", eventCategory);
            if (attributes.containsKey("attendanceEventReason")) {
                String eventReason = (String) attributes.get("attendanceEventReason");
                event.put("reason", eventReason);
            }

                NeutralQuery query = new NeutralQuery(1);
                query.addCriteria(new NeutralCriteria(BATCH_JOB_ID_KEY, NeutralCriteria.OPERATOR_EQUAL, getBatchJobId(), false));
                query.addCriteria(new NeutralCriteria("studentId", NeutralCriteria.OPERATOR_EQUAL, studentId));
                query.addCriteria(new NeutralCriteria("schoolId", NeutralCriteria.OPERATOR_EQUAL, schoolId));
                query.addCriteria(new NeutralCriteria("schoolYear", NeutralCriteria.OPERATOR_EQUAL, schoolYear));

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

                WriteResult writeResult = getNeutralRecordMongoAccess().getRecordRepository().updateMulti(query, update, ATTENDANCE_TRANSFORMED);
                
                if (writeResult.getField("updatedExisting").equals(Boolean.FALSE)) {
                    NeutralRecord record = new NeutralRecord();
                    record.setRecordId(type1UUIDGeneratorStrategy.generateId().toString());
                    record.setRecordType(ATTENDANCE_TRANSFORMED);
                    record.setBatchJobId(getBatchJobId());
                    List<Map<String, Object>> attendanceEvents = new ArrayList<Map<String, Object>>();
                    attendanceEvents.add (event);

                    Map<String, Object> attendanceAttributes = new HashMap<String, Object>();
                    attendanceAttributes.put("studentId", studentId);
                    attendanceAttributes.put("schoolId", schoolId);
                    attendanceAttributes.put("schoolYear", schoolYear);
                    attendanceAttributes.put("attendanceEvent", attendanceEvents);
                    record.setAttributes(attendanceAttributes);
                    record.setSourceFile(attendances.values().iterator().next().getSourceFile());
                    record.setLocationInSourceFile(attendances.values().iterator().next().getLocationInSourceFile());
                    record.setCreationTime(getWorkNote().getRangeMinimum());

                    insertRecord(record);
                }
        }

        LOG.info("Finished transforming attendance data");
    }


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
