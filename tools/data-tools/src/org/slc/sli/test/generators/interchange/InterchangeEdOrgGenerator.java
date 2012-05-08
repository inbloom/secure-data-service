package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.List;

import org.slc.sli.test.edfi.entities.Course;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrganization;
import org.slc.sli.test.edfi.entities.LocalEducationAgency;
import org.slc.sli.test.edfi.entities.School;
import org.slc.sli.test.edfi.entities.StateEducationAgency;
import org.slc.sli.test.edfi.entities.relations.CourseMeta;
import org.slc.sli.test.edfi.entities.relations.LeaMeta;
import org.slc.sli.test.edfi.entities.relations.SchoolMeta;
import org.slc.sli.test.edfi.entities.relations.SeaMeta;
import org.slc.sli.test.generators.CourseGenerator;
import org.slc.sli.test.generators.LocalEducationAgencyGenerator;
import org.slc.sli.test.generators.SchoolGenerator;
import org.slc.sli.test.generators.StateEducationAgencyGenerator;
import org.slc.sli.test.mappingGenerator.MetaRelations;
import org.slc.sli.test.mappingGenerator.StateEdFiXmlGenerator;

/**
 * Generates all Education Organizations contained in the variables:
 * - seaMap
 * - leaMap
 * - schoolMap
 * - courseMap
 * as created by the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator
 *
 * @author dduran
 *
 */
public class InterchangeEdOrgGenerator {

    /**
     * Sets up a new Education Organization Interchange and populates it
     *
     * @return
     */
    public static InterchangeEducationOrganization generate() {
<<<<<<< HEAD
        long startTime = System.currentTimeMillis();
=======
>>>>>>> master

        InterchangeEducationOrganization interchange = new InterchangeEducationOrganization();
        List<Object> interchangeObjects = interchange
                .getStateEducationAgencyOrEducationServiceCenterOrFeederSchoolAssociation();

        addEntitiesToInterchange(interchangeObjects);

<<<<<<< HEAD
        System.out.println("generated " + interchangeObjects.size() + " InterchangeEducationOrganization entries in: "
                + (System.currentTimeMillis() - startTime));
=======
>>>>>>> master
        return interchange;
    }

    /**
     * Generates the individual entities that can be Educational Organizations
     *
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<Object> interchangeObjects) {

        generateStateEducationAgencies(interchangeObjects, MetaRelations.SEA_MAP.values());

        generateLocalEducationAgencies(interchangeObjects, MetaRelations.LEA_MAP.values());

        generateSchools(interchangeObjects, MetaRelations.SCHOOL_MAP.values());

        generateCourses(interchangeObjects, MetaRelations.COURSE_MAP.values());
    }

    /**
     * Loops all SEAs and, using an SEA Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param seaMetas
     */
    private static void generateStateEducationAgencies(List<Object> interchangeObjects, Collection<SeaMeta> seaMetas) {
<<<<<<< HEAD
=======
        long startTime = System.currentTimeMillis();

>>>>>>> master
        for (SeaMeta seaMeta : seaMetas) {

            StateEducationAgency sea;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                sea = null;
            } else {
                sea = StateEducationAgencyGenerator.generateLowFi(seaMeta.id);
            }

            interchangeObjects.add(sea);
        }
<<<<<<< HEAD
=======

        System.out.println("generated " + seaMetas.size() + " StateEducationAgency objects in: "
                + (System.currentTimeMillis() - startTime));
>>>>>>> master
    }

    /**
     * Loops all LEAs and, using an LEA Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param leaMetas
     */
    private static void generateLocalEducationAgencies(List<Object> interchangeObjects, Collection<LeaMeta> leaMetas) {
<<<<<<< HEAD
=======
        long startTime = System.currentTimeMillis();

>>>>>>> master
        for (LeaMeta leaMeta : leaMetas) {

            LocalEducationAgency lea;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                lea = null;
            } else {
                lea = LocalEducationAgencyGenerator.generateLowFi(leaMeta.id, leaMeta.seaId);
            }

            interchangeObjects.add(lea);
        }
<<<<<<< HEAD
=======

        System.out.println("generated " + leaMetas.size() + " LocalEducationAgency objects in: "
                + (System.currentTimeMillis() - startTime));
>>>>>>> master
    }

    /**
     * Loops all schools and, using a School Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param schoolMetas
     */
    private static void generateSchools(List<Object> interchangeObjects, Collection<SchoolMeta> schoolMetas) {
<<<<<<< HEAD
=======
        long startTime = System.currentTimeMillis();

>>>>>>> master
        for (SchoolMeta schoolMeta : schoolMetas) {

            School school;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                school = null;
            } else {
                school = SchoolGenerator.generateLowFi(schoolMeta.id, schoolMeta.leaId);
            }

            interchangeObjects.add(school);
        }
<<<<<<< HEAD
=======

        System.out.println("generated " + schoolMetas.size() + " School objects in: "
                + (System.currentTimeMillis() - startTime));
>>>>>>> master
    }

    /**
     * Loops all courses and, using a Course Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param courseMetas
     */
    private static void generateCourses(List<Object> interchangeObjects, Collection<CourseMeta> courseMetas) {
<<<<<<< HEAD
=======
        long startTime = System.currentTimeMillis();

>>>>>>> master
        for (CourseMeta courseMeta : courseMetas) {

            Course course;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                course = null;
            } else {
                course = CourseGenerator.generateLowFi(courseMeta.id, courseMeta.schoolId);
            }

            interchangeObjects.add(course);
        }
<<<<<<< HEAD
=======

        System.out.println("generated " + courseMetas.size() + " Course objects in: "
                + (System.currentTimeMillis() - startTime));
>>>>>>> master
    }
}
