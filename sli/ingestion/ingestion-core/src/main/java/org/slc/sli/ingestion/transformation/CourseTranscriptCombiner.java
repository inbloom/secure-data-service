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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slc.sli.ingestion.NeutralRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Transformer for CourseTranscript Entities
 *
 * @author jcole
 * @author shalka
 */
@Scope("prototype")
@Component("courseTranscriptTransformationStrategy")
public class CourseTranscriptCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(CourseTranscriptCombiner.class);

    private static final String COURSE_TRANSCRIPT = "courseTranscript";
    private static final String COURSE_TRANSCRIPT_TRANSFORMED = "courseTranscript_transformed";

    private Map<Object, NeutralRecord> courseTranscripts;
    private List<NeutralRecord> transformedCourseTranscripts;

    /**
     * Default constructor.
     */
    public CourseTranscriptCombiner() {
        this.courseTranscripts = new HashMap<Object, NeutralRecord>();
        this.transformedCourseTranscripts = new ArrayList<NeutralRecord>();
    }

    /**
     * The chaining of transformation steps. This implementation assumes that all data will be
     * processed in "one-go"
     */
    @Override
    public void performTransformation() {
        loadData();
        transform();
        insertRecords(transformedCourseTranscripts, COURSE_TRANSCRIPT_TRANSFORMED);
    }

    /**
     * Pre-requisite interchanges for student transcript data to be successfully transformed:
     * student
     */
    public void loadData() {
        LOG.info("Loading data for courseTranscript transformation.");
        this.courseTranscripts = getCollectionFromDb(COURSE_TRANSCRIPT);
        LOG.info("{} is loaded into local storage.  Total Count = {}", COURSE_TRANSCRIPT,
                courseTranscripts.size());
    }

    /**
     * Transforms student transcript association data to pass SLI data validation and writes into
     * staging mongo db.
     */
    public void transform() {
        LOG.info("Transforming course transcript data");
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : courseTranscripts.entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            if (attributes.get("creditsAttempted") == null) {
                attributes.remove("creditsAttempted");
            }

            if (attributes.get("gradeType") == null) {
                attributes.put("gradeType", "Final");
            }
            neutralRecord.setRecordType(neutralRecord.getRecordType() + "_transformed");
            neutralRecord.setCreationTime(getWorkNote().getRangeMinimum());
            transformedCourseTranscripts.add(neutralRecord);
        }
        LOG.info("Finished transforming student transcript association data");
    }
}
