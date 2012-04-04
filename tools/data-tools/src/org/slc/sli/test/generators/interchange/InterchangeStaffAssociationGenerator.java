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
import org.slc.sli.test.mappingGenerator.StateEdFiXmlGenerator;

/**
 * Generates the Staff Association Interchange as derived from the associations
 * determined during the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator.
 *
 * @author dduran
 *
 */
public class InterchangeStaffAssociationGenerator {

    /**
     * Sets up a new Staff Association Interchange and populates it.
     *
     * @return
     */
    public static InterchangeStaffAssociation generate() {
        long startTime = System.currentTimeMillis();

        InterchangeStaffAssociation interchange = new InterchangeStaffAssociation();
        List<Object> interchangeObjects = interchange
                .getStaffOrStaffEducationOrgEmploymentAssociationOrStaffEducationOrgAssignmentAssociation();

        addEntitiesToInterchange(interchangeObjects);

        System.out.println("generated " + interchangeObjects.size() + " InterchangeStaffAssociation entries in: "
                + (System.currentTimeMillis() - startTime));
        return interchange;
    }

    /**
     * Generate the individual Teacher Association entities.
     *
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<Object> interchangeObjects) {

        generateTeachersAndAssoc(interchangeObjects, MetaRelations.TEACHER_MAP.values());

    }

    /**
     * Loops all teachers and, using a Teacher Generator, populates interchange data.
     * Also loops teacher-school and teacher-section associations and populates
     * the same.
     *
     * @param interchangeObjects
     * @param teacherMetas
     */
    private static void generateTeachersAndAssoc(List<Object> interchangeObjects, Collection<TeacherMeta> teacherMetas) {

        for (TeacherMeta teacherMeta : teacherMetas) {

            Teacher teacher;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                teacher = null;
            } else {
                teacher = TeacherGenerator.generateLowFi(teacherMeta.id);
            }

            interchangeObjects.add(teacher);

            generateTeacherSchoolAssoc(interchangeObjects, teacherMeta);

            generateTeacherSectionAssoc(interchangeObjects, teacherMeta);
        }
    }

    private static void generateTeacherSchoolAssoc(List<Object> interchangeObjects, TeacherMeta teacherMeta) {
        for (String schoolId : teacherMeta.schoolIds) {

            TeacherSchoolAssociation teacherSchool;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                teacherSchool = null;
            } else {
                teacherSchool = TeacherSchoolAssociationGenerator.generateLowFi(teacherMeta, schoolId);
            }

            interchangeObjects.add(teacherSchool);
        }
    }

    private static void generateTeacherSectionAssoc(List<Object> interchangeObjects, TeacherMeta teacherMeta) {
        for (String sectionId : teacherMeta.sectionIds) {

            TeacherSectionAssociation teacherSection;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                teacherSection = null;
            } else {
                teacherSection = TeacherSectionAssociationGenerator.generateLowFi(teacherMeta, sectionId);
            }

            interchangeObjects.add(teacherSection);
        }
    }
}
