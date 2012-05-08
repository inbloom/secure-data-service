package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.InterchangeStudent;
import org.slc.sli.test.edfi.entities.Student;
import org.slc.sli.test.edfi.entities.relations.StudentMeta;
import org.slc.sli.test.generators.FastStudentGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;
import org.slc.sli.test.mappingGenerator.StateEdFiXmlGenerator;

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
<<<<<<< HEAD
        long startTime = System.currentTimeMillis();
=======
>>>>>>> master

        InterchangeStudent interchange = new InterchangeStudent();
        List<Student> interchangeObjects = interchange.getStudent();

        addEntitiesToInterchange(interchangeObjects);

<<<<<<< HEAD
        System.out.println("generated " + interchangeObjects.size() + " InterchangeStudent entries in: " + (System.currentTimeMillis() - startTime));
=======
>>>>>>> master
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
<<<<<<< HEAD
=======
        long startTime = System.currentTimeMillis();
>>>>>>> master

        for (StudentMeta studentMeta : studentMetas) {

            Student student;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                student = null;
            } else {
                student = FastStudentGenerator.generateLowFi(studentMeta.id);
            }

            interchangeObjects.add(student);

        }

<<<<<<< HEAD
=======
        System.out.println("generated " + studentMetas.size() + " Student objects in: "
                + (System.currentTimeMillis() - startTime));
>>>>>>> master
    }

}
