package org.slc.sli.test.xmlgen;

import java.io.File;
import java.io.PrintStream;

import javax.xml.stream.XMLStreamWriter;

import org.slc.sli.test.edfi.entities.InterchangeAssessmentMetadata;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrgCalendar;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrganization;
import org.slc.sli.test.edfi.entities.InterchangeMasterSchedule;
import org.slc.sli.test.edfi.entities.InterchangeStaffAssociation;
import org.slc.sli.test.edfi.entities.InterchangeStudentAssessment;
import org.slc.sli.test.edfi.entities.InterchangeStudentAttendance;
import org.slc.sli.test.edfi.entities.InterchangeStudentCohort;
import org.slc.sli.test.edfi.entities.InterchangeStudentDiscipline;
import org.slc.sli.test.edfi.entities.InterchangeStudentEnrollment;
import org.slc.sli.test.edfi.entities.InterchangeStudentGrade;
import org.slc.sli.test.edfi.entities.InterchangeStudentParent;
import org.slc.sli.test.edfi.entities.InterchangeStudentProgram;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.generators.interchange.InterchangeAssessmentMetadataGenerator;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgCalGenerator;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgGenerator;
import org.slc.sli.test.generators.interchange.InterchangeMasterScheduleGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStaffAssociationGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentAssessmentGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentAttendanceGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentCohortGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentDisciplineGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentEnrollmentGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentGradeGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentParentGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentProgramGenerator;
import org.slc.sli.test.utils.DataUtils;
import org.slc.sli.test.utils.JaxbUtils;
import org.slc.sli.test.utils.ValidateSchema;

/**
 * Code to generate referentially correct interchanges that are built
 * as a depth-first walk of the dependency graph beginning with
 * StateEducationAgency.
 *
 * @author dduran
 *
 */
public class StateEdFiXmlGenerator {

    /**
     * used by interchange generators to determine which entity generator to invoke
     */
    //public static String fidelityOfData = "medium";
    public static String fidelityOfData = "low";


    /**
     * used to determine the output directory for generated interchange and control files
     */
    public static String rootOutputPath = "./data";

    /**
     * Currently generates:
     * - edOrg
     * - edOrgCalendar
     * - masterSchedule
     * - staffAssociation
     * - student
     * - studentEnrollment
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        processProgramArguments(args);

        MetaRelations.construct();

        generateAndMarshalInterchanges();

        ValidateSchema.check(rootOutputPath);

    }

    private static void processProgramArguments(String[] args) {

        if (args.length > 0 && ("low".equals(args[0]) || "medium".equals(args[0]))) {
            fidelityOfData = args[0];
        }
        if (args.length > 1) {
            rootOutputPath = args[1];
        }
        System.out.println("will use " + fidelityOfData + " fidelity data generators.");

        if (new File(rootOutputPath).mkdirs()) {
            System.out.println("created directory: " + rootOutputPath);
        }
        System.out.println("root output path: " + rootOutputPath);
    }

    private static void generateAndMarshalInterchanges() throws Exception {

       edOrg();

       edOrgCalendar();

       masterSchedule();

       staffAssociation();

       studentParent();

       studentEnrollment();

       studentProgram();

       studentCohort();

       studentDiscipline();

       studentAttendance();

       assessmentMetaData();

       studentAssessment();

       studentGrade();

    }

    /**
     * Generate InterchangeEducationOrganization data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void edOrg() throws Exception {

        InterchangeEducationOrganization edOrg = InterchangeEdOrgGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeEducationOrganization.xml";

        JaxbUtils.marshal(edOrg, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "EducationOrganization", xmlFilePath);
    }

    /**
     * Generate InterchangeEducationOrgCalendar data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void edOrgCalendar() throws Exception {

        InterchangeEducationOrgCalendar edOrgCal = InterchangeEdOrgCalGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeEducationOrgCalendar.xml";

        JaxbUtils.marshal(edOrgCal, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "EducationOrgCalendar", xmlFilePath);
    }

    /**
     * Generate InterchangeMasterSchedule data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void masterSchedule() throws Exception {

        InterchangeMasterSchedule masterSchedule = InterchangeMasterScheduleGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeMasterSchedule.xml";

        JaxbUtils.marshal(masterSchedule, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "MasterSchedule", xmlFilePath);
    }

    /**
     * Generate InterchangeStaffAssociation data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void staffAssociation() throws Exception {

        InterchangeStaffAssociation staffAssociation = InterchangeStaffAssociationGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeStaffAssociation.xml";

        JaxbUtils.marshal(staffAssociation, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StaffAssociation", xmlFilePath);
    }

    /**
     * Generate InterchangeStudentParent data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */

    private static void studentParent() throws Exception {

        InterchangeStudentParent studentParent = InterchangeStudentParentGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeStudentParent.xml";

        JaxbUtils.marshal(studentParent, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StudentParent", xmlFilePath);

    }

    /**
     * Generate InterchangeStudentEnrollment data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentEnrollment() throws Exception {
    	System.out.println("start to student enrollment");

        InterchangeStudentEnrollment studentEnrollment = InterchangeStudentEnrollmentGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeStudentEnrollment.xml";

        JaxbUtils.marshal(studentEnrollment, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StudentEnrollment", xmlFilePath);
    }

    /**
     * Generate InterchangeStudentAttendance data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentAttendance() throws Exception {

        String xmlFilePath = rootOutputPath + "/InterchangeStudentAttendance.xml";

        XMLStreamWriter writer = JaxbUtils.createInterchangeWriter(xmlFilePath, InterchangeStudentAttendance.class);
        InterchangeStudentAttendanceGenerator.generate(writer);
        JaxbUtils.finishInterchangeWriter(writer);

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "Attendance", xmlFilePath);
    }

    /**
     * Generate InterchangeStudentProgram data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentProgram() throws Exception {

        InterchangeStudentProgram studentProgram = InterchangeStudentProgramGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeStudentProgram.xml";

        JaxbUtils.marshal(studentProgram, new PrintStream(xmlFilePath));

        // TODO: uncomment when ingestion supports this
        // DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StudentProgram",
        // xmlFilePath);
    }

    /**
     * Generate InterchangeStudentCohort data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentCohort() throws Exception {

        InterchangeStudentCohort studentCohort = InterchangeStudentCohortGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeStudentCohort.xml";

        JaxbUtils.marshal(studentCohort, new PrintStream(xmlFilePath));

        // TODO: uncomment when ingestion supports this
        // DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StudentCohort",
        // xmlFilePath);
    }

    /**
     * Generate InterchangeStudentDiscipline data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentDiscipline() throws Exception {

        InterchangeStudentDiscipline studentDiscipline = InterchangeStudentDisciplineGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeStudentDiscipline.xml";

        JaxbUtils.marshal(studentDiscipline, new PrintStream(xmlFilePath));

        // TODO: uncomment when ingestion supports this
        // DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StudentCohort",
        // xmlFilePath);
    }

    /**
     * Generate InterchangeAssessmentMetadata data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void assessmentMetaData() throws Exception {

        InterchangeAssessmentMetadata assessmentMetadata = InterchangeAssessmentMetadataGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeAssessmentMetadata.xml";

        JaxbUtils.marshal(assessmentMetadata, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "AssessmentMetadata", xmlFilePath);
    }

    /**
     * Generate InterchangeStudentAssessment data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentAssessment() throws Exception {

        String xmlFilePath = rootOutputPath + "/InterchangeStudentAssessment.xml";

        XMLStreamWriter writer = JaxbUtils.createInterchangeWriter(xmlFilePath, InterchangeStudentAssessment.class);
        InterchangeStudentAssessmentGenerator.generate(writer);
        JaxbUtils.finishInterchangeWriter(writer);

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StudentAssessment", xmlFilePath);
    }

    /**
     * Generate InterchangeStudentGrade data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentGrade() throws Exception {

        String xmlFilePath = rootOutputPath + "/InterchangeStudentGrade.xml";

        XMLStreamWriter writer = JaxbUtils.createInterchangeWriter(xmlFilePath, InterchangeStudentGrade.class);
        InterchangeStudentGradeGenerator.generate(writer);
        JaxbUtils.finishInterchangeWriter(writer);
        
        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StudentGrades", xmlFilePath);
    }

}
