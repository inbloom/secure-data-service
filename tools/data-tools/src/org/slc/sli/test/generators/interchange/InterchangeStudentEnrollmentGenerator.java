package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.InterchangeStudentEnrollment;
import org.slc.sli.test.edfi.entities.StudentSchoolAssociation;
import org.slc.sli.test.edfi.entities.StudentSectionAssociation;
import org.slc.sli.test.edfi.entities.relations.StudentMeta;
import org.slc.sli.test.generators.StudentSchoolAssociationGenerator;
import org.slc.sli.test.generators.StudentSectionAssociationGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;

public class InterchangeStudentEnrollmentGenerator {

    public static InterchangeStudentEnrollment generate() {

        InterchangeStudentEnrollment interchange = new InterchangeStudentEnrollment();
        List<Object> interchangeObjects = interchange
                .getStudentSchoolAssociationOrStudentSectionAssociationOrGraduationPlan();

        addEntitiesToInterchange(interchangeObjects);

        return interchange;
    }

    private static void addEntitiesToInterchange(List<Object> interchangeObjects) {

        generateStudentAssocs(interchangeObjects, MetaRelations.studentMap.values());

    }

    private static void generateStudentAssocs(List<Object> interchangeObjects, Collection<StudentMeta> studentMetas) {

        for (StudentMeta studentMeta : studentMetas) {

            for (String schoolId : studentMeta.schoolIds) {
                StudentSchoolAssociation studentSchool = StudentSchoolAssociationGenerator.generate(studentMeta.id,
                        schoolId);

                interchangeObjects.add(studentSchool);
            }

            for (String sectionId : studentMeta.sectionIds) {

                // TODO: need to take another look at SectionIdentity and constructing it fully
                StudentSectionAssociation studentSection = StudentSectionAssociationGenerator.generate(studentMeta.id,
                        studentMeta.schoolIds.get(0), sectionId);

                interchangeObjects.add(studentSection);
            }
        }

    }

}
