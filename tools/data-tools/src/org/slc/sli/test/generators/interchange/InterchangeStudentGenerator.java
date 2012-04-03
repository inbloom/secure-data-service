package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.InterchangeStudent;
import org.slc.sli.test.edfi.entities.Student;
import org.slc.sli.test.edfi.entities.relations.StudentMeta;
import org.slc.sli.test.generators.FastStudentGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;

public class InterchangeStudentGenerator {

    public static InterchangeStudent generate() {

        InterchangeStudent interchange = new InterchangeStudent();
        List<Student> interchangeObjects = interchange.getStudent();

        addEntitiesToInterchange(interchangeObjects);

        return interchange;
    }

    private static void addEntitiesToInterchange(List<Student> interchangeObjects) {

        generateStudents(interchangeObjects, MetaRelations.studentMap.values());

    }

    private static void generateStudents(List<Student> interchangeObjects, Collection<StudentMeta> studentMetas) {

        for (StudentMeta studentMeta : studentMetas) {

            Student student = FastStudentGenerator.generate(studentMeta.id);
            interchangeObjects.add(student);

        }

    }

}
