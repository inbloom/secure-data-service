package org.slc.sli.test.generators.interchange;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.Program;
import org.slc.sli.test.edfi.entities.Course;
import org.slc.sli.test.edfi.entities.EducationServiceCenter;
import org.slc.sli.test.edfi.entities.FeederSchoolAssociation;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrganization;
import org.slc.sli.test.edfi.entities.LocalEducationAgency;
import org.slc.sli.test.edfi.entities.School;
import org.slc.sli.test.edfi.entities.StateEducationAgency;
import org.slc.sli.test.edfi.entities.meta.CourseMeta;
import org.slc.sli.test.edfi.entities.meta.ESCMeta;
import org.slc.sli.test.edfi.entities.meta.LeaMeta;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.SchoolMeta;
import org.slc.sli.test.edfi.entities.meta.SeaMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.CourseGenerator;
import org.slc.sli.test.generators.EducationAgencyGenerator;
import org.slc.sli.test.generators.LocalEducationAgencyGenerator;
import org.slc.sli.test.generators.ProgramGenerator;
import org.slc.sli.test.generators.SchoolGenerator;
import org.slc.sli.test.generators.StateEducationAgencyGenerator;
import org.slc.sli.test.xmlgen.StateEdFiXmlGenerator;

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


	static CourseGenerator gen ;

	static {
		try
		{
			gen = new CourseGenerator(GradeLevelType.SEVENTH_GRADE);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
     * Sets up a new Education Organization Interchange and populates it
     *
     * @return
     * @throws Exception
     */
    public static InterchangeEducationOrganization generate() throws Exception {

        
        InterchangeEducationOrganization interchange = new InterchangeEducationOrganization();
        List<Object> interchangeObjects = interchange
                .getStateEducationAgencyOrEducationServiceCenterOrFeederSchoolAssociation();

        addEntitiesToInterchange(interchangeObjects);

        return interchange;
    }

    /**
     * Generates the individual entities that can be Educational Organizations
     *
     * @param interchangeObjects
     * @throws Exception
     */
    private static void addEntitiesToInterchange(List<Object> interchangeObjects) throws Exception {

        generateStateEducationAgencies(interchangeObjects, MetaRelations.SEA_MAP.values());

        generateEducationServiceCenters(interchangeObjects, MetaRelations.ESC_MAP.values());
        
        generateFeederSchoolAssociation(interchangeObjects, MetaRelations.SCHOOL_MAP.values());
        
        generateLocalEducationAgencies(interchangeObjects, MetaRelations.LEA_MAP.values());

        generateSchools(interchangeObjects, MetaRelations.SCHOOL_MAP.values());

        generateCourses(interchangeObjects, MetaRelations.COURSE_MAP.values());

        generatePrograms(interchangeObjects, MetaRelations.PROGRAM_MAP.values());

    }

    /**
     * Loops all SEAs and, using an SEA Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param seaMetas
     */
    private static void generateStateEducationAgencies(List<Object> interchangeObjects, Collection<SeaMeta> seaMetas) {
        long startTime = System.currentTimeMillis();

        for (SeaMeta seaMeta : seaMetas) {

            StateEducationAgency sea;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                sea = StateEducationAgencyGenerator.generateLowFi(seaMeta.id, seaMeta);
            } else {
            	sea = StateEducationAgencyGenerator.generateLowFi(seaMeta.id, seaMeta);
            }

            interchangeObjects.add(sea);
        }

        System.out.println("generated " + seaMetas.size() + " StateEducationAgency objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all ESCs and, using an ESC Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param escMetas
     */
    private static void generateEducationServiceCenters(List<Object> interchangeObjects, Collection<ESCMeta> escMetas) {
        long startTime = System.currentTimeMillis();

        for (ESCMeta escMeta : escMetas) {

            EducationServiceCenter esc;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                esc = EducationAgencyGenerator.getEducationServiceCenter(escMeta.id, escMeta.seaId);
            } else {
                esc = EducationAgencyGenerator.getEducationServiceCenter(escMeta.id, escMeta.seaId);
            }

            interchangeObjects.add(esc);
        }

        System.out.println("generated " + escMetas.size() + " EducationServiceCenter objects in: "
                + (System.currentTimeMillis() - startTime));
    }
    
    /**
     * Generates FEEDER_RELATIONSHIPS FeederSchoolAssociation between 2 schools using a circular list.
     *
     * @param interchangeObjects
     * @param seaMetas
     */
    private static void generateFeederSchoolAssociation(List<Object> interchangeObjects, Collection<SchoolMeta> schools) {
        long startTime = System.currentTimeMillis();

        List<SchoolMeta> schoolMetas = new LinkedList<SchoolMeta>(schools);
        int schoolCount = schoolMetas.size();
        if(schoolCount > 1) {
            for(int i = 0; i < MetaRelations.FEEDER_RELATIONSHIPS; i++) {
                SchoolMeta feederMeta   = schoolMetas.get(i % schoolCount);
                SchoolMeta receiverMeta = schoolMetas.get((i+ 1) % schoolCount);
                FeederSchoolAssociation fsa = EducationAgencyGenerator.getFeederSchoolAssociation(receiverMeta, feederMeta);
                fsa.setFeederRelationshipDescription("Feeder Relationship " +  i);
                interchangeObjects.add(fsa);
            }
        }

        System.out.println("generated " + MetaRelations.FEEDER_RELATIONSHIPS + " FeederSchoolAssociation objects in: "
                + (System.currentTimeMillis() - startTime));
    }
    
    /**
     * Loops all LEAs and, using an LEA Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param leaMetas
     */
    private static void generateLocalEducationAgencies(List<Object> interchangeObjects, Collection<LeaMeta> leaMetas) {
        long startTime = System.currentTimeMillis();

        for (LeaMeta leaMeta : leaMetas) {

            LocalEducationAgency lea;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
            	lea = LocalEducationAgencyGenerator.generateMedFi(leaMeta.id, leaMeta.seaId, leaMeta);
            } else {
            	lea = LocalEducationAgencyGenerator.generateMedFi(leaMeta.id, leaMeta.seaId, leaMeta);
            }

            interchangeObjects.add(lea);
        }

        System.out.println("generated " + leaMetas.size() + " LocalEducationAgency objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all schools and, using a School Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param schoolMetas
     */
    private static void generateSchools(List<Object> interchangeObjects, Collection<SchoolMeta> schoolMetas) {
        long startTime = System.currentTimeMillis();

        for (SchoolMeta schoolMeta : schoolMetas) {

            School school;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                school = null;
            } else {
                school = SchoolGenerator.generateLowFi(schoolMeta.id, schoolMeta.leaId, schoolMeta.programId);
            }

            interchangeObjects.add(school);
        }

        System.out.println("generated " + schoolMetas.size() + " School objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all courses and, using a Course Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param courseMetas
     * @throws Exception
     */
    private static void generateCourses(List<Object> interchangeObjects, Collection<CourseMeta> courseMetas) throws Exception {
        long startTime = System.currentTimeMillis();
        for (CourseMeta courseMeta : courseMetas) {

            Course course;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
            	//course = CourseGenerator.generateMidumFi(courseMeta.id, courseMeta.schoolId);
            	course = null;
            } else {
                //course = CourseGenerator.generateLowFi(courseMeta.id, courseMeta.schoolId);
                course = gen.getCourse(courseMeta.id, courseMeta.schoolId);
            }

            courseMeta.courseCodes.addAll(course.getCourseCode());

            interchangeObjects.add(course);
        }

        System.out.println("generated " + courseMetas.size() + " Course objects in: "
                + (System.currentTimeMillis() - startTime));
    }

    /**
     * Loops all programs and, using a Program Generator, populates interchange data.
     *
     * @param interchangeObjects
     * @param programMetas
     */
    private static void generatePrograms(List<Object> interchangeObjects, Collection<ProgramMeta> programMetas) {
        for (ProgramMeta programMeta : programMetas) {

            Program program;

            if ("medium".equals(StateEdFiXmlGenerator.fidelityOfData)) {
                program = null;
            } else {
                program = ProgramGenerator.generateLowFi(programMeta.id);
            }

            interchangeObjects.add(program);
        }
    }
}
