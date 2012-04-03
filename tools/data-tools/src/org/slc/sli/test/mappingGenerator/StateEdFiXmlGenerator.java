package org.slc.sli.test.mappingGenerator;

import java.io.FileNotFoundException;
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

        try {
			JaxbUtils.marshal(edOrg, new PrintStream("data/InterchangeEducationOrganization.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        JaxbUtils.marshal(edOrg);
        

        System.out.println("Marshalled edOrg interchange in: " + (System.currentTimeMillis() - genObjectTime));
    }

    private static void edOrgCalendar() {
        long startTime = System.currentTimeMillis();

        InterchangeEducationOrgCalendar edOrgCal = InterchangeEdOrgCalGenerator.generate();

        long genObjectTime = System.currentTimeMillis();
        System.out.println("Constructed edOrgCalendar interchange in: " + (System.currentTimeMillis() - startTime));

        JaxbUtils.marshal(edOrgCal);
        
        try {
			JaxbUtils.marshal(edOrgCal, new PrintStream("data/InterchangeEducationOrgCalendar.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("Marshalled edOrgCalendar interchange in: " + (System.currentTimeMillis() - genObjectTime));
    }

    private static void masterSchedule() {
        long startTime = System.currentTimeMillis();

        InterchangeMasterSchedule masterSchedule = InterchangeMasterScheduleGenerator.generate();

        long genObjectTime = System.currentTimeMillis();
        System.out.println("Constructed masterSchedule interchange in: " + (System.currentTimeMillis() - startTime));

        JaxbUtils.marshal(masterSchedule);
        
        try {
			JaxbUtils.marshal(masterSchedule, new PrintStream("data/InterchangeMasterSchedule.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("Marshalled masterSchedule interchange in: " + (System.currentTimeMillis() - genObjectTime));
    }

    private static void staffAssociation() {
        long startTime = System.currentTimeMillis();

        InterchangeStaffAssociation staffAssociation = InterchangeStaffAssociationGenerator.generate();

        long genObjectTime = System.currentTimeMillis();
        System.out.println("Constructed staffAssociation interchange in: " + (System.currentTimeMillis() - startTime));

        JaxbUtils.marshal(staffAssociation);
        
        try {
			JaxbUtils.marshal(staffAssociation, new PrintStream("data/InterchangeStaffAssociation.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        System.out.println("Marshalled staffAssociation interchange in: "
                + (System.currentTimeMillis() - genObjectTime));
    }
}
