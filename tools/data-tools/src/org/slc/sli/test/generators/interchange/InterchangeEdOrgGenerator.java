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

public class InterchangeEdOrgGenerator {

    public static InterchangeEducationOrganization generate() {

        InterchangeEducationOrganization interchange = new InterchangeEducationOrganization();
        List<Object> interchangeObjects = interchange
                .getStateEducationAgencyOrEducationServiceCenterOrFeederSchoolAssociation();

        generateStateEducationAgencies(interchangeObjects, MetaRelations.seaMap.values());

        generateLocalEducationAgencies(interchangeObjects, MetaRelations.leaMap.values());

        generateSchools(interchangeObjects, MetaRelations.schoolMap.values());

        generateCourses(interchangeObjects, MetaRelations.courseMap.values());

        return interchange;
    }

    private static void generateStateEducationAgencies(List<Object> interchangeObjects, Collection<SeaMeta> seaMetas) {
        for (SeaMeta seaMeta : seaMetas) {
            StateEducationAgency sea = StateEducationAgencyGenerator.getStateEducationAgency(seaMeta.id);
            interchangeObjects.add(sea);
        }
    }

    private static void generateLocalEducationAgencies(List<Object> interchangeObjects, Collection<LeaMeta> leaMetas) {
        for (LeaMeta leaMeta : leaMetas) {
            LocalEducationAgency lea = LocalEducationAgencyGenerator.getLocalEducationAgency(leaMeta.id, leaMeta.seaId);
            interchangeObjects.add(lea);
        }
    }

    private static void generateSchools(List<Object> interchangeObjects, Collection<SchoolMeta> schoolMetas) {
        for (SchoolMeta schoolMeta : schoolMetas) {
            School school = SchoolGenerator.getSchool(schoolMeta.id, schoolMeta.leaId);
            interchangeObjects.add(school);
        }
    }

    private static void generateCourses(List<Object> interchangeObjects, Collection<CourseMeta> courseMetas) {
        for (CourseMeta courseMeta : courseMetas) {
            Course course = CourseGenerator.getFastCourse(courseMeta.id, courseMeta.schoolId);
            interchangeObjects.add(course);
        }
    }
}
