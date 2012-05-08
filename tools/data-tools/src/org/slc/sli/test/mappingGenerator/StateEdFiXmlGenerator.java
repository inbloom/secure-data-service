package org.slc.sli.test.mappingGenerator;

<<<<<<< HEAD
=======
import java.io.File;
>>>>>>> master
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
<<<<<<< HEAD
    public static String outputPath = "./data/";
    		
=======
    public static String rootOutputPath = "./data";

>>>>>>> master
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

<<<<<<< HEAD
        ValidateSchema.check(outputPath);
=======
        ValidateSchema.check(rootOutputPath);
>>>>>>> master

    }

    private static void processProgramArguments(String[] args) {

        if (args.length > 0 && ("low".equals(args[0]) || "medium".equals(args[0]))) {
            fidelityOfData = args[0];
        }
<<<<<<< HEAD
        System.out.println("will use " + fidelityOfData + " fidelity data generators.");
=======
        if (args.length > 1) {
            rootOutputPath = args[1];
        }
        System.out.println("will use " + fidelityOfData + " fidelity data generators.");

        if (new File(rootOutputPath).mkdirs()) {
            System.out.println("created directory: " + rootOutputPath);
        }
        System.out.println("root output path: " + rootOutputPath);
>>>>>>> master
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

<<<<<<< HEAD
        JaxbUtils.marshal(edOrg, new PrintStream(outputPath + "InterchangeEducationOrganization.xml"));

        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "EducationOrganization", outputPath + "InterchangeEducationOrganization.xml");
=======
        String xmlFilePath = rootOutputPath + "/InterchangeEducationOrganization.xml";

        JaxbUtils.marshal(edOrg, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "EducationOrganization", xmlFilePath);
>>>>>>> master
    }

    /**
     * Generate InterchangeEducationOrgCalendar data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void edOrgCalendar() throws Exception {

        InterchangeEducationOrgCalendar edOrgCal = InterchangeEdOrgCalGenerator.generate();

<<<<<<< HEAD
        JaxbUtils.marshal(edOrgCal, new PrintStream(outputPath + "InterchangeEducationOrgCalendar.xml"));

        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "EducationOrgCalendar", outputPath + "InterchangeEducationOrgCalendar.xml");
=======
        String xmlFilePath = rootOutputPath + "/InterchangeEducationOrgCalendar.xml";

        JaxbUtils.marshal(edOrgCal, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "EducationOrgCalendar", xmlFilePath);
>>>>>>> master
    }

    /**
     * Generate InterchangeMasterSchedule data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void masterSchedule() throws Exception {

        InterchangeMasterSchedule masterSchedule = InterchangeMasterScheduleGenerator.generate();

<<<<<<< HEAD
        JaxbUtils.marshal(masterSchedule, new PrintStream(outputPath + "InterchangeMasterSchedule.xml"));

        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "MasterSchedule", outputPath + "InterchangeMasterSchedule.xml");
=======
        String xmlFilePath = rootOutputPath + "/InterchangeMasterSchedule.xml";

        JaxbUtils.marshal(masterSchedule, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "MasterSchedule", xmlFilePath);
>>>>>>> master
    }

    /**
     * Generate InterchangeStaffAssociation data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void staffAssociation() throws Exception {

        InterchangeStaffAssociation staffAssociation = InterchangeStaffAssociationGenerator.generate();

<<<<<<< HEAD
        JaxbUtils.marshal(staffAssociation, new PrintStream(outputPath + "InterchangeStaffAssociation.xml"));
        
        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "StaffAssociation", outputPath + "InterchangeStaffAssociation.xml");
=======
        String xmlFilePath = rootOutputPath + "/InterchangeStaffAssociation.xml";

        JaxbUtils.marshal(staffAssociation, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StaffAssociation", xmlFilePath);
>>>>>>> master
    }

    /**
     * Generate InterchangeStudent data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void student() throws Exception {

        InterchangeStudent student = InterchangeStudentGenerator.generate();

<<<<<<< HEAD
        JaxbUtils.marshal(student, new PrintStream(outputPath + "InterchangeStudent.xml"));

        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "Student", outputPath + "InterchangeStudent.xml");
=======
        String xmlFilePath = rootOutputPath + "/InterchangeStudent.xml";

        JaxbUtils.marshal(student, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "Student", xmlFilePath);
>>>>>>> master
    }

    /**
     * Generate InterchangeStudentEnrollment data and use Jaxb to output the XML file.
     *
     * @throws Exception
     */
    private static void studentEnrollment() throws Exception {

        InterchangeStudentEnrollment studentEnrollment = InterchangeStudentEnrollmentGenerator.generate();

<<<<<<< HEAD
        JaxbUtils.marshal(studentEnrollment, new PrintStream(outputPath + "InterchangeStudentEnrollment.xml"));

        DataUtils.writeControlFile(outputPath + "MainControlFile.ctl", "StudentEnrollment", outputPath + "InterchangeStudentEnrollment.xml");
=======
        String xmlFilePath = rootOutputPath + "/InterchangeStudentEnrollment.xml";

        JaxbUtils.marshal(studentEnrollment, new PrintStream(xmlFilePath));

        DataUtils.writeControlFile(rootOutputPath + "/MainControlFile.ctl", "StudentEnrollment", xmlFilePath);
>>>>>>> master
    }
}
