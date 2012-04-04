package org.slc.sli.test.mappingGenerator;

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
    public static String outputPath = "./data/";
    		
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

        ValidateSchema.check(outputPath);

    }

    private static void processProgramArguments(String[] args) {

        if (args.length > 0 && ("low".equals(args[0]) || "medium".equals(args[0]))) {
            fidelityOfData = args[0];
        }
        System.out.println("will use " + fidelityOfData + " fidelity data generators.");
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

        JaxbUtils.marshal(edOrg, new PrintStream(outputPath + "InterchangeEducationOrganization.xml"));

        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "EducationOrganization", outputPath + "InterchangeEducationOrganization.xml");
    }

    /**
     * Generate InterchangeEducationOrgCalendar data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void edOrgCalendar() throws Exception {

        InterchangeEducationOrgCalendar edOrgCal = InterchangeEdOrgCalGenerator.generate();

        JaxbUtils.marshal(edOrgCal, new PrintStream(outputPath + "InterchangeEducationOrgCalendar.xml"));

        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "EducationOrgCalendar", outputPath + "InterchangeEducationOrgCalendar.xml");
    }

    /**
     * Generate InterchangeMasterSchedule data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void masterSchedule() throws Exception {

        InterchangeMasterSchedule masterSchedule = InterchangeMasterScheduleGenerator.generate();

        JaxbUtils.marshal(masterSchedule, new PrintStream(outputPath + "InterchangeMasterSchedule.xml"));

        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "MasterSchedule", outputPath + "InterchangeMasterSchedule.xml");
    }

    /**
     * Generate InterchangeStaffAssociation data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void staffAssociation() throws Exception {

        InterchangeStaffAssociation staffAssociation = InterchangeStaffAssociationGenerator.generate();

        JaxbUtils.marshal(staffAssociation, new PrintStream(outputPath + "InterchangeStaffAssociation.xml"));
        
        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "StaffAssociation", outputPath + "InterchangeStaffAssociation.xml");
    }

    /**
     * Generate InterchangeStudent data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void student() throws Exception {

        InterchangeStudent student = InterchangeStudentGenerator.generate();

        JaxbUtils.marshal(student, new PrintStream(outputPath + "InterchangeStudent.xml"));

        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "Student", outputPath + "InterchangeStudent.xml");
    }

    /**
     * Generate InterchangeStudentEnrollment data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentEnrollment() throws Exception {

        InterchangeStudentEnrollment studentEnrollment = InterchangeStudentEnrollmentGenerator.generate();

        JaxbUtils.marshal(studentEnrollment, new PrintStream(outputPath + "InterchangeStudentEnrollment.xml"));

        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "StudentEnrollment", outputPath + "InterchangeStudentEnrollment.xml");
    }
}
