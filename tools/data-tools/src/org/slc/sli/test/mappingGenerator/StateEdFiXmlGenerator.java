package org.slc.sli.test.mappingGenerator;

import org.slc.sli.test.edfi.entities.InterchangeEducationOrgCalendar;
import org.slc.sli.test.edfi.entities.InterchangeEducationOrganization;
import org.slc.sli.test.edfi.entities.InterchangeMasterSchedule;
import org.slc.sli.test.edfi.entities.InterchangeStaffAssociation;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgCalGenerator;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgGenerator;
import org.slc.sli.test.generators.interchange.InterchangeMasterScheduleGenerator;
import org.slc.sli.test.generators.interchange.InterchangeStaffAssociationGenerator;
import org.slc.sli.test.util.JaxbUtils;

public class StateEdFiXmlGenerator {

    public static void main(String[] args) {

        MetaRelations.buildFromSea();

        edOrg();

        edOrgCalendar();

        masterSchedule();

        staffAssociation();

    }

    private static void edOrg() {
        long startTime = System.currentTimeMillis();

        InterchangeEducationOrganization edOrg = InterchangeEdOrgGenerator.generate();

        long genObjectTime = System.currentTimeMillis();
        System.out.println("Constructed edOrg interchange in: " + (System.currentTimeMillis() - startTime));

        JaxbUtils.marshal(edOrg);

        System.out.println("Marshalled edOrg interchange in: " + (System.currentTimeMillis() - genObjectTime));
    }

    private static void edOrgCalendar() {
        long startTime = System.currentTimeMillis();

        InterchangeEducationOrgCalendar edOrgCal = InterchangeEdOrgCalGenerator.generate();

        long genObjectTime = System.currentTimeMillis();
        System.out.println("Constructed edOrgCalendar interchange in: " + (System.currentTimeMillis() - startTime));

        JaxbUtils.marshal(edOrgCal);

        System.out.println("Marshalled edOrgCalendar interchange in: " + (System.currentTimeMillis() - genObjectTime));
    }

    private static void masterSchedule() {
        long startTime = System.currentTimeMillis();

        InterchangeMasterSchedule masterSchedule = InterchangeMasterScheduleGenerator.generate();

        long genObjectTime = System.currentTimeMillis();
        System.out.println("Constructed masterSchedule interchange in: " + (System.currentTimeMillis() - startTime));

        JaxbUtils.marshal(masterSchedule);

        System.out.println("Marshalled masterSchedule interchange in: " + (System.currentTimeMillis() - genObjectTime));
    }

    private static void staffAssociation() {
        long startTime = System.currentTimeMillis();

        InterchangeStaffAssociation staffAssociation = InterchangeStaffAssociationGenerator.generate();

        long genObjectTime = System.currentTimeMillis();
        System.out.println("Constructed staffAssociation interchange in: " + (System.currentTimeMillis() - startTime));

        JaxbUtils.marshal(staffAssociation);

        System.out.println("Marshalled staffAssociation interchange in: "
                + (System.currentTimeMillis() - genObjectTime));
    }
}
