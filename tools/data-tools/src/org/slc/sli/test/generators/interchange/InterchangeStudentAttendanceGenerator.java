package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import org.slc.sli.test.edfi.entities.AttendanceEvent;
import org.slc.sli.test.edfi.entities.InterchangeStudentAttendance;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.AttendanceEventGenerator;
import org.slc.sli.test.utils.InterchangeWriter;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Student Attendance Interchange as derived from the associations
 * determined during the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator.
 *
 * @author lchen
 */
public class InterchangeStudentAttendanceGenerator {

    /**
     * Sets up a new Student Attendance Interchange and populates it.
     *
     * @return
     */
    public static void generate(InterchangeWriter<InterchangeStudentAttendance> writer) {
        writeEntitiesToInterchange(writer);        
    }

    /**
     * Generate the individual Student Attendance Association entities and write them out.
     *
     */
    private static void writeEntitiesToInterchange(InterchangeWriter<InterchangeStudentAttendance> writer) {

        generateStudentAttendanceEventAssoc(MetaRelations.STUDENT_MAP.values(), writer);

    }

    private static void generateStudentAttendanceEventAssoc(Collection<StudentMeta> studentMetas, 
            InterchangeWriter<InterchangeStudentAttendance> writer) {
        long startTime = System.currentTimeMillis();

        int objGenCounter = 0;
        for (StudentMeta studentMeta : studentMetas) {

            AttendanceEventGenerator.resetCalendar();

            for (String sectionId : studentMeta.sectionIds) {


                for (int count = 0; count < MetaRelations.ATTENDANCE_PER_STUDENT_SECTION; count++) {

                    AttendanceEvent attendanceEvent;

                    if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                        attendanceEvent = null;
                    } else {
                        attendanceEvent = AttendanceEventGenerator.generateLowFi(studentMeta.id,
                                studentMeta.schoolIds.get(0), sectionId);
                    }

                    writer.marshal(attendanceEvent);

                    objGenCounter++;
                }
            }
       }

        System.out.println("generated " + objGenCounter + " AttendanceEvent objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
