package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.InterchangeStaffAssociation;
import org.slc.sli.test.edfi.entities.Teacher;
import org.slc.sli.test.edfi.entities.TeacherSchoolAssociation;
import org.slc.sli.test.edfi.entities.TeacherSectionAssociation;
import org.slc.sli.test.edfi.entities.relations.TeacherMeta;
import org.slc.sli.test.generators.TeacherGenerator;
import org.slc.sli.test.generators.TeacherSchoolAssociationGenerator;
import org.slc.sli.test.generators.TeacherSectionAssociationGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;

public class InterchangeStaffAssociationGenerator {

    public static InterchangeStaffAssociation generate() {

        InterchangeStaffAssociation interchange = new InterchangeStaffAssociation();
        List<Object> interchangeObjects = interchange
                .getStaffOrStaffEducationOrgEmploymentAssociationOrStaffEducationOrgAssignmentAssociation();

        addEntitiesToInterchange(interchangeObjects);

        return interchange;
    }

    private static void addEntitiesToInterchange(List<Object> interchangeObjects) {

        generateTeachersAndAssoc(interchangeObjects, MetaRelations.teacherMap.values());

    }

    private static void generateTeachersAndAssoc(List<Object> interchangeObjects, Collection<TeacherMeta> teacherMetas) {

        for (TeacherMeta teacherMeta : teacherMetas) {

            Teacher teacher = TeacherGenerator.getFastTeacher(teacherMeta.id);
            interchangeObjects.add(teacher);

            for (String schoolId : teacherMeta.schoolIds) {
                TeacherSchoolAssociation teacherSchool = TeacherSchoolAssociationGenerator
                        .getFastTeacherSchoolAssociation(teacherMeta, schoolId);

                interchangeObjects.add(teacherSchool);
            }

            for (String sectionId : teacherMeta.sectionIds) {
                TeacherSectionAssociation teacherSection = TeacherSectionAssociationGenerator
                        .getFastTeacherSectionAssociation(teacherMeta, sectionId);

                interchangeObjects.add(teacherSection);
            }
        }
    }

}
