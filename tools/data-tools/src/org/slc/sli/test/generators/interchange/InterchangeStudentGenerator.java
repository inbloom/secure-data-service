package org.slc.sli.test.generators.interchange;

import static org.slc.sli.test.utils.InterchangeWriter.REPORT_INDENTATION;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.InterchangeStudent;
import org.slc.sli.test.edfi.entities.Student;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.FastStudentGenerator;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

/**
 * Generates the Master Schedule Interchange as derived from the variable:
 * - studentMap
 * as created by the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator
 *
 * @author dduran
 *
 */
public class InterchangeStudentGenerator {

    /**
     * Sets up a new Student Interchange and populates it
     *
     * @return
     */
    public static InterchangeStudent generate() {
        long startTime = System.currentTimeMillis();

        InterchangeStudent interchange = new InterchangeStudent();
        List<Student> interchangeObjects = interchange.getStudent();

        System.out.println(interchange.getClass().getSimpleName() + ": started");

        addEntitiesToInterchange(interchangeObjects);

        System.out.println(interchange.getClass().getSimpleName() + ": generated " + interchangeObjects.size() + 
                " entries in " + (System.currentTimeMillis() - startTime) + "\n");
        return interchange;
    }

    /**
     * Generates the individual entities that can generate a Student
     *
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<Student> interchangeObjects) {

        generateStudents(interchangeObjects, MetaRelations.STUDENT_MAP.values());

    }

    /**
     * Loops all students and, using an Fast Student Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param studentMetas
     */
    private static void generateStudents(List<Student> interchangeObjects, Collection<StudentMeta> studentMetas) {
        long startTime = System.currentTimeMillis();

        for (StudentMeta studentMeta : studentMetas) {

            Student student;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                student = null;
            } else {
                student = FastStudentGenerator.generateLowFi(studentMeta.id);
            }

            interchangeObjects.add(student);

        }

        System.out.println(REPORT_INDENTATION + "generated " + studentMetas.size() + " Student objects in: "
                + (System.currentTimeMillis() - startTime));
    }

}
