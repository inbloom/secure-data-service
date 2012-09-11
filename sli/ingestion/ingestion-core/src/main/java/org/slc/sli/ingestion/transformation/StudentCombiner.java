package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.NeutralRecord;

/**
 *
 * @author joechung
 *
 */
@Scope("prototype")
@Component("studentTransformationStrategy")
public class StudentCombiner extends AbstractTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(StudentCombiner.class);
    @Override
    protected void performTransformation() {
        LOG.info("Transforming student data");
        List<NeutralRecord> transformedStudents = new ArrayList<NeutralRecord>();
        Map<Object, NeutralRecord> students = getCollectionFromDb("student");
        for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : students.entrySet()) {
            NeutralRecord neutralRecord = neutralRecordEntry.getValue();
            Map<String, Object> attributes = neutralRecord.getAttributes();
            attributes.put("joeTest", "1234");
            transformedStudents.add(neutralRecord);
        }
        insertRecords(transformedStudents, "student_transformed");
        LOG.info("Completed transforming student data");
    }

}
