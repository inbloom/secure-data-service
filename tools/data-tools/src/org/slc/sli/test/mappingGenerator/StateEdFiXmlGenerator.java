package org.slc.sli.test.mappingGenerator;

import java.io.File;
import java.io.PrintStream;

import org.slc.sli.test.edfi.entities.InterchangeEducationOrgCalendar;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrganization;
import org.slc.sli.test.edfi.entities.InterchangeMasterSchedule;
import org.slc.sli.test.edfi.entities.InterchangeStaffAssociation;
import org.slc.sli.test.edfi.entities.InterchangeStudent;
import org.slc.sli.test.edfi.entities.InterchangeStudentEnrollment;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgCalGenerator;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgGenerator;
import org.slc.sli.test.generators.interchange.InterchangeMasterScheduleGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStaffAssociationGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentEnrollmentGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStudentGenerator;
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

        MetaRelations.buildFromSea();

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

        student();

        studentEnrollment();
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
     * Generate InterchangeStudent data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void student() throws Exception {

        InterchangeStudent student = InterchangeStudentGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeStudent.xml";

        JaxbUtils.marshal(student, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "Student", xmlFilePath);
    }

    /**
     * Generate InterchangeStudentEnrollment data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentEnrollment() throws Exception {

        InterchangeStudentEnrollment studentEnrollment = InterchangeStudentEnrollmentGenerator.generate();

        String xmlFilePath = rootOutputPath + "/InterchangeStudentEnrollment.xml";

        JaxbUtils.marshal(studentEnrollment, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StudentEnrollment", xmlFilePath);
    }
}
