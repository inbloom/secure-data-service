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
import org.slc.sli.test.utils.JaxbUtils;

/**
 * Code to generate referentially correct interchanges that are built
 * as a depth-first walk of the dependency graph beginning with
 * StateEducationAgency.
 * @author dduran
 *
 */
public class StateEdFiXmlGenerator {

	/**
	 * Currently generates:
	 * 	- edOrg
	 * 	- edOrgCalendar
	 * 	- masterSchedule
	 *  - staffAssociation
	 * @param args
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception {

        MetaRelations.buildFromSea();

        edOrg();

        edOrgCalendar();

        masterSchedule();

        staffAssociation();

        student();

        studentEnrollment();

    }

    /**
     * Generate InterchangeEducationOrganization data and use Jaxb to output the XML file.
     * @throws Exception
     */
    private static void edOrg() throws Exception {

        InterchangeEducationOrganization edOrg = InterchangeEdOrgGenerator.generate();

        JaxbUtils.marshal(edOrg, new PrintStream("data/InterchangeEducationOrganization.xml"));

    }

    /**
     * Generate InterchangeEducationOrgCalendar data and use Jaxb to output the XML file.
     * @throws Exception
     */
    private static void edOrgCalendar() throws Exception {

        InterchangeEducationOrgCalendar edOrgCal = InterchangeEdOrgCalGenerator.generate();

        JaxbUtils.marshal(edOrgCal, new PrintStream("data/InterchangeEducationOrgCalendar.xml"));

    }

    /**
     * Generate InterchangeMasterSchedule data and use Jaxb to output the XML file.
     * @throws Exception
     */
    private static void masterSchedule() throws Exception {

        InterchangeMasterSchedule masterSchedule = InterchangeMasterScheduleGenerator.generate();

        JaxbUtils.marshal(masterSchedule, new PrintStream("data/InterchangeMasterSchedule.xml"));

    }

    /**
     * Generate InterchangeStaffAssociation data and use Jaxb to output the XML file.
     * @throws Exception
     */
    private static void staffAssociation() throws Exception {

        InterchangeStaffAssociation staffAssociation = InterchangeStaffAssociationGenerator.generate();

        JaxbUtils.marshal(staffAssociation, new PrintStream("data/InterchangeStaffAssociation.xml"));
    }

    private static void student() throws Exception {

        InterchangeStudent student = InterchangeStudentGenerator.generate();

        JaxbUtils.marshal(student, new PrintStream("data/InterchangeStudent.xml"));

    }

    private static void studentEnrollment() throws Exception {

        InterchangeStudentEnrollment studentEnrollment = InterchangeStudentEnrollmentGenerator.generate();

        JaxbUtils.marshal(studentEnrollment, new PrintStream("data/InterchangeStudentEnrollment.xml"));

    }
}
