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

/**
 * Generates all Education Organizations contained in the variables:
 *  - seaMap
 *  - leaMap
 *  - schoolMap
 *  - courseMap
 *  as created by the call to MetaRelations.buildFromSea() in StateEdFiXmlGenerator
 * @author dduran
 *
 */
public class InterchangeEdOrgGenerator {

	/**
	 * Sets up a new Education Organization Interchange and populates it
	 * @return
	 */
    public static InterchangeEducationOrganization generate() {
        long startTime = System.currentTimeMillis();

        InterchangeEducationOrganization interchange = new InterchangeEducationOrganization();
        List<Object> interchangeObjects = interchange
                .getStateEducationAgencyOrEducationServiceCenterOrFeederSchoolAssociation();

        addEntitiesToInterchange(interchangeObjects);

        System.out.println("generated InterchangeEducationOrganization object in: "
                + (System.currentTimeMillis() - startTime));
        return interchange;
    }

    /**
     * Generates the individual entities that can be Educational Organizations
     * @param interchangeObjects
     */
    private static void addEntitiesToInterchange(List<Object> interchangeObjects) {

        generateStateEducationAgencies(interchangeObjects, MetaRelations.seaMap.values());

        generateLocalEducationAgencies(interchangeObjects, MetaRelations.leaMap.values());

        generateSchools(interchangeObjects, MetaRelations.schoolMap.values());

        generateCourses(interchangeObjects, MetaRelations.courseMap.values());
    }

    /**
     * Loops all SEAs and, using an SEA Generator, populates interchange data.
     * @param interchangeObjects
     * @param seaMetas
     */
    private static void generateStateEducationAgencies(List<Object> interchangeObjects, Collection<SeaMeta> seaMetas) {
        for (SeaMeta seaMeta : seaMetas) {
            StateEducationAgency sea = StateEducationAgencyGenerator.getStateEducationAgency(seaMeta.id);
            interchangeObjects.add(sea);
        }
    }

    /**
     * Loops all LEAs and, using an LEA Generator, populates interchange data.
     * @param interchangeObjects
     * @param leaMetas
     */
    private static void generateLocalEducationAgencies(List<Object> interchangeObjects, Collection<LeaMeta> leaMetas) {
        for (LeaMeta leaMeta : leaMetas) {
            LocalEducationAgency lea = LocalEducationAgencyGenerator.getLocalEducationAgency(leaMeta.id, leaMeta.seaId);
            interchangeObjects.add(lea);
        }
    }

    /**
     * Loops all schools and, using a School Generator, populates interchange data.
     * @param interchangeObjects
     * @param schoolMetas
     */
    private static void generateSchools(List<Object> interchangeObjects, Collection<SchoolMeta> schoolMetas) {
        for (SchoolMeta schoolMeta : schoolMetas) {
            School school = SchoolGenerator.getSchool(schoolMeta.id, schoolMeta.leaId);
            interchangeObjects.add(school);
        }
    }

    /**
     * Loops all courses and, using a Course Generator, populates interchange data.
     * @param interchangeObjects
     * @param courseMetas
     */
    private static void generateCourses(List<Object> interchangeObjects, Collection<CourseMeta> courseMetas) {
        for (CourseMeta courseMeta : courseMetas) {
            Course course = CourseGenerator.getFastCourse(courseMeta.id, courseMeta.schoolId);
            interchangeObjects.add(course);
        }
    }
}
