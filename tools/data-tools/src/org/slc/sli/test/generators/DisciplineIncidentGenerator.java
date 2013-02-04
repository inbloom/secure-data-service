/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.test.generators;

import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.BehaviorDescriptorType;
import org.slc.sli.test.edfi.entities.SLCDisciplineIncident;
import org.slc.sli.test.edfi.entities.SLCDisciplineIncidentIdentityType;
import org.slc.sli.test.edfi.entities.SLCDisciplineIncidentReferenceType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SecondaryBehavior;
import org.slc.sli.test.edfi.entities.SLCStaffIdentityType;
import org.slc.sli.test.edfi.entities.SLCStaffReferenceType;
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

    static Random rand = new Random(31);
    private static String date = "2011-03-04";
    private static String time = "09:00:00";

    /**
     * Generates a DisciplineIncident from a DisciplineIncidentMeta.
     *
     * @param meta
     *
     * @return <code>DisciplineIncident</code>
     */
    public static SLCDisciplineIncident generateLowFi(DisciplineIncidentMeta meta) {
        String disciplineIncidentId = meta.id;
        String schoolId = meta.schoolId;
        String staffId = meta.staffId;

        return generateLowFi(disciplineIncidentId, schoolId, staffId);
    }

    /**
     * Generates a DisciplineIncidentReferenceType
     *
     * @param disciplineIncidentId
     * @param schoolId
     * @return
     */
    public static SLCDisciplineIncidentReferenceType generateReference(String disciplineIncidentId, String schoolId) {
        SLCDisciplineIncidentReferenceType dirt = new SLCDisciplineIncidentReferenceType();
        SLCDisciplineIncidentIdentityType diit = new SLCDisciplineIncidentIdentityType();
        dirt.setDisciplineIncidentIdentity(diit);
        diit.setIncidentIdentifier(disciplineIncidentId);
        SLCEducationalOrgIdentityType edOrgIdentity = new SLCEducationalOrgIdentityType();
        edOrgIdentity.setStateOrganizationId(schoolId);
        SLCEducationalOrgReferenceType edOrgRef = new SLCEducationalOrgReferenceType();
        edOrgRef.setEducationalOrgIdentity(edOrgIdentity);
        diit.setEducationalOrgReference(edOrgRef);

        return dirt;
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
    public static SLCDisciplineIncident generateLowFi(String disciplineIncidentId, String schoolId, String staffId) {

        SLCDisciplineIncident incident = new SLCDisciplineIncident();

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
        SLCEducationalOrgIdentityType edOrgIdentity = new SLCEducationalOrgIdentityType();
        edOrgIdentity.setStateOrganizationId(schoolId);
//        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        SLCEducationalOrgReferenceType schoolRef = new SLCEducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        incident.setSchoolReference(schoolRef);

        // construct and add the staff reference
        if (staffId != null && staffId.length() > 0) {
            SLCStaffIdentityType sit = new SLCStaffIdentityType();
            sit.setStaffUniqueStateId(staffId);
            SLCStaffReferenceType srt = new SLCStaffReferenceType();
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
