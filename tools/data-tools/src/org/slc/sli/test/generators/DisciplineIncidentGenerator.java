package org.slc.sli.test.generators;

import java.util.Random;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.BehaviorCategoryType;
import org.slc.sli.test.edfi.entities.BehaviorDescriptorType;
import org.slc.sli.test.edfi.entities.DisciplineIncident;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.SecondaryBehavior;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.WeaponsType;
import org.slc.sli.test.edfi.entities.meta.DisciplineIncidentMeta;

/**
 * Generates DisciplineIncident data
 *
 * @author slee
 *
 */
public class DisciplineIncidentGenerator {
    private static final Logger log = Logger.getLogger(DisciplineIncidentGenerator.class);

    static Random rand = new Random();
    private static String date = "2011-03-04";
    private static String time = "09:00:00";

    /**
     * Generates a DisciplineIncident from a DisciplineIncidentMeta.
     *
     * @param meta
     *
     * @return <code>DisciplineIncident</code>
     */
    public static DisciplineIncident generateLowFi(DisciplineIncidentMeta meta) {
        String disciplineIncidentId = meta.id;
        String schoolId = meta.schoolId;
        String staffId = meta.staffId;

        return generateLowFi(disciplineIncidentId, schoolId, staffId);
    }

    /**
     * Generates a DisciplineIncident between a disciplineIncidentId and a schoolId
     * with a staffId as an optional reference.
     *
     * @param cohortId
     * @param schoolId
     * @param staffId
     *
     * @return <code>DisciplineIncident</code>
     */
    public static DisciplineIncident generateLowFi(String disciplineIncidentId, String schoolId, String staffId) {

        DisciplineIncident incident = new DisciplineIncident();

        incident.setIncidentIdentifier(disciplineIncidentId);
        incident.setId(disciplineIncidentId);
        // set incident date and time
        incident.setIncidentDate(date);
        incident.setIncidentTime(time);
        incident.setIncidentLocation(GeneratorUtils.generateIncidentLocationType());
        incident.setReporterDescription(GeneratorUtils.generateReporterDescriptionType());
        incident.setReporterName("Discipline Incident Reporter-" + disciplineIncidentId);
        incident.setReportedToLawEnforcement(new Boolean(rand.nextBoolean()));

        // add Behaviors
        // ObjectFactory factory = new ObjectFactory();
        int count = 0;
        double prob = 1.0D / BehaviorDescriptor.values().length;
        for (BehaviorDescriptor behaviorDescriptor : BehaviorDescriptor.values()) {
            if (rand.nextDouble() < prob || count == (BehaviorDescriptor.values().length - 1)) {
                BehaviorDescriptorType behaviorDescriptorType = new BehaviorDescriptorType();
                // JAXBElement<String> behaviorDescriptorCode =
                // factory.createBehaviorDescriptorTypeCodeValue(behaviorDescriptor.codeValue);
                // JAXBElement<String> behaviorDescriptorShortDescription =
                // factory.createBehaviorDescriptorTypeShortDescription(behaviorDescriptor.shortDescription);
                // JAXBElement<String> behaviorDescriptorDescription =
                // factory.createBehaviorDescriptorTypeDescription(behaviorDescriptor.description);
                // behaviorDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(behaviorDescriptorCode);
                // behaviorDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(behaviorDescriptorShortDescription);
                // behaviorDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(behaviorDescriptorDescription);
                behaviorDescriptorType.setCodeValue(behaviorDescriptor.codeValue);
//                behaviorDescriptorType.setShortDescription(behaviorDescriptor.shortDescription);
//                behaviorDescriptorType.setDescription(behaviorDescriptor.description);
                incident.getBehaviors().add(behaviorDescriptorType);
                break;
            }
            ++count;
        }

        // add SecondaryBehavior
        SecondaryBehavior sb = new SecondaryBehavior();
        sb.setSecondaryBehavior("SecondaryBehavior-" + rand.nextInt());
        sb.setBehaviorCategory(GeneratorUtils.generateBehaviorCategoryType());
        incident.getSecondaryBehaviors().add(sb);

        incident.setCaseNumber("CaseNumber-" + rand.nextInt(99999999));
        WeaponsType wt = new WeaponsType();
        wt.getWeapon().add(GeneratorUtils.generateWeaponItemType());
        incident.setWeapons(wt);
        // construct and add the school references
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.setStateOrganizationId(schoolId);
//        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        incident.setSchoolReference(schoolRef);

        // construct and add the staff reference
        if (staffId != null && staffId.length() > 0) {
            StaffIdentityType sit = new StaffIdentityType();
            sit.setStaffUniqueStateId(staffId);
            StaffReferenceType srt = new StaffReferenceType();
            srt.setStaffIdentity(sit);
            incident.setStaffReference(srt);
        }

        return incident;
    }

    // BehaviorDescriptor for DisciplineIncident.
    public enum BehaviorDescriptor {
        MINOR("BEHAVIOR 001", "Behavior 001 description", "Minor behavior description"), BULLY("BEHAVIOR 002",
                "Behavior 002 description", "Bully behavior description"), ORAL("BEHAVIOR 003",
                "Behavior 003 description", "Oral behavior description"), VIOLENT("BEHAVIOR 004",
                "Behavior 004 description", "Violent behavior description");

        String codeValue;
        String shortDescription;
        String description;

        BehaviorDescriptor(String cv, String sd, String d) {
            codeValue = cv;
            shortDescription = sd;
            description = d;
        }

        public String getCodeValue() {
            return codeValue;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public String getDescription() {
            return description;
        }
    }
}
