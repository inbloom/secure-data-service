package org.slc.sli.test.mappingGenerator;

import java.io.PrintStream;

import org.slc.sli.test.edfi.entities.InterchangeEducationOrgCalendar;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrganization;
import org.slc.sli.test.edfi.entities.InterchangeMasterSchedule;
import org.slc.sli.test.edfi.entities.InterchangeStaffAssociation;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgCalGenerator;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgGenerator;
import org.slc.sli.test.generators.interchange.InterchangeMasterScheduleGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStaffAssociationGenerator;
import org.slc.sli.test.utils.JaxbUtils;

public class StateEdFiXmlGenerator {

    public static void main(String[] args) throws Exception {

        MetaRelations.buildFromSea();

        edOrg();

        edOrgCalendar();

        masterSchedule();

        staffAssociation();

    }

    private static void edOrg() throws Exception {

        InterchangeEducationOrganization edOrg = InterchangeEdOrgGenerator.generate();

        JaxbUtils.marshal(edOrg, new PrintStream("data/InterchangeEducationOrganization.xml"));

    }

    private static void edOrgCalendar() throws Exception {

        InterchangeEducationOrgCalendar edOrgCal = InterchangeEdOrgCalGenerator.generate();

        JaxbUtils.marshal(edOrgCal, new PrintStream("data/InterchangeEducationOrgCalendar.xml"));

    }

    private static void masterSchedule() throws Exception {

        InterchangeMasterSchedule masterSchedule = InterchangeMasterScheduleGenerator.generate();

        JaxbUtils.marshal(masterSchedule, new PrintStream("data/InterchangeMasterSchedule.xml"));

    }

    private static void staffAssociation() throws Exception {

        InterchangeStaffAssociation staffAssociation = InterchangeStaffAssociationGenerator.generate();

        JaxbUtils.marshal(staffAssociation, new PrintStream("data/InterchangeStaffAssociation.xml"));
    }
}
