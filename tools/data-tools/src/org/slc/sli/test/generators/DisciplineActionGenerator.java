package org.slc.sli.test.generators;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Random;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.DisciplineAction;
import org.slc.sli.test.edfi.entities.DisciplineDescriptorType;
import org.slc.sli.test.edfi.entities.DisciplineIncidentIdentityType;
import org.slc.sli.test.edfi.entities.DisciplineIncidentReferenceType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.meta.DisciplineActionMeta;

/**
 * Generates DisciplineIncident data
 *
 * @author slee
 *
 */
public class DisciplineActionGenerator {
    private static final Logger log = Logger.getLogger(DisciplineActionGenerator.class);

    static Random rand = new Random();
    private static String date = "2011-03-04";

    /**
     * Generates a DisciplineAction from a DisciplineActionMeta.
     *
     * @param meta
     *
     * @return <code>DisciplineAction</code>
     */
    public static DisciplineAction generateLowFi(DisciplineActionMeta meta) {
        String disciplineActionId = meta.id;
        String schoolId = meta.schoolId;
        Collection<String> incidentIds = meta.incidentIds;
        Collection<String> studentIds = meta.studentIds;
        Collection<String> staffIds = meta.staffIds;

        return generateLowFi(disciplineActionId, incidentIds, studentIds, staffIds, schoolId);
    }

    /**
     * Generates a DisciplineAction for a disciplineActionId
     * with a list of disciplineIncidentIds, a list of studentIds, a list of staffIds
     * and a responsibilitySchoolId.
     *
     * @param disciplineActionId
     * @param disciplineIncidentIds
     * @param studentIds
     * @param staffIds
     * @param responsibilitySchoolId
     *
     * @return <code>DisciplineAction</code>
     */
    public static DisciplineAction generateLowFi(String disciplineActionId, Collection<String> disciplineIncidentIds,
            Collection<String> studentIds, Collection<String> staffIds, String responsibilitySchoolId) {
        DisciplineAction action = generateLowFi(disciplineActionId, disciplineIncidentIds, studentIds,
                responsibilitySchoolId);

        // construct and add the staff reference
        for (String staffId : staffIds) {
            StaffIdentityType sit = new StaffIdentityType();
            sit.setStaffUniqueStateId(staffId);
            StaffReferenceType srt = new StaffReferenceType();
            srt.setStaffIdentity(sit);
            action.getStaffReference().add(srt);
        }

        return action;
    }

    /**
     * Generates a DisciplineAction for a disciplineActionId
     * with a list of disciplineIncidentIds, a list of studentIds,
     * and a responsibilitySchoolId as references.
     *
     * @param disciplineActionId
     * @param disciplineIncidentIds
     * @param studentIds
     * @param responsibilitySchoolId
     *
     * @return <code>DisciplineAction</code>
     */
    public static DisciplineAction generateLowFi(String disciplineActionId, Collection<String> disciplineIncidentIds,
            Collection<String> studentIds, String responsibilitySchoolId) {
        DisciplineAction action = basicLowFiFactory(disciplineActionId, responsibilitySchoolId);

        // construct and add the disciplineIncident reference
        for (String disciplineIncidentId : disciplineIncidentIds) {
            DisciplineIncidentReferenceType dirt = new DisciplineIncidentReferenceType();
            DisciplineIncidentIdentityType diit = new DisciplineIncidentIdentityType();
            diit.setIncidentIdentifier(disciplineIncidentId);
            diit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(responsibilitySchoolId);
            dirt.setDisciplineIncidentIdentity(diit);
            action.getDisciplineIncidentReference().add(dirt);
        }

        // construct and add the student reference
        for (String studentId : studentIds) {
            StudentIdentityType sit = new StudentIdentityType();
            sit.setStudentUniqueStateId(studentId);
            StudentReferenceType srt = new StudentReferenceType();
            srt.setStudentIdentity(sit);
            action.getStudentReference().add(srt);
        }

        return action;
    }

    /**
     * Generates a DisciplineAction for a disciplineActionId
     * with a disciplineIncidentId, studentId, responsibilitySchoolId and assignmentSchoolId as
     * references.
     *
     * @param disciplineActionId
     * @param disciplineIncidentId
     * @param studentId
     * @param responsibilitySchoolId
     * @param assignmentSchoolId
     *
     * @return <code>DisciplineAction</code>
     */
    public static DisciplineAction generateLowFi(String disciplineActionId, String disciplineIncidentId,
            String studentId, String responsibilitySchoolId, String assignmentSchoolId) {
        DisciplineAction action = generateLowFi(disciplineActionId, disciplineIncidentId, studentId,
                responsibilitySchoolId);

        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.setStateOrganizationId(assignmentSchoolId);
        // edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(assignmentSchoolId);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        action.setAssignmentSchoolReference(schoolRef);

        return action;
    }

    /**
     * Generates a DisciplineAction for a disciplineActionId
     * with a disciplineIncidentId, studentId and responsibilitySchoolId as references.
     *
     * @param disciplineActionId
     * @param disciplineIncidentId
     * @param studentId
     * @param responsibilitySchoolId
     *
     * @return <code>DisciplineAction</code>
     */
    public static DisciplineAction generateLowFi(String disciplineActionId, String disciplineIncidentId,
            String studentId, String responsibilitySchoolId) {
        DisciplineAction action = basicLowFiFactory(disciplineActionId, responsibilitySchoolId);

        // construct and add the disciplineIncident reference
        DisciplineIncidentReferenceType dirt = new DisciplineIncidentReferenceType();
        DisciplineIncidentIdentityType diit = new DisciplineIncidentIdentityType();
        diit.setIncidentIdentifier(disciplineIncidentId);
        diit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(responsibilitySchoolId);
        dirt.setDisciplineIncidentIdentity(diit);
        action.getDisciplineIncidentReference().add(dirt);

        // construct and add the student reference
        StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        action.getStudentReference().add(srt);

        return action;
    }

    /**
     * Generates a DisciplineAction for a disciplineActionId
     * with a responsibilitySchoolId as a reference.
     *
     * @param disciplineActionId
     * @param responsibilitySchoolId
     *
     * @return <code>DisciplineAction</code>
     */
    private static DisciplineAction basicLowFiFactory(String disciplineActionId, String responsibilitySchoolId) {

        DisciplineAction action = new DisciplineAction();

        action.setDisciplineActionIdentifier(disciplineActionId);
        // set incident date and time
        action.setDisciplineDate(date);
        action.setDisciplineActionLength(BigInteger.valueOf(rand.nextInt(100)));
        action.setActualDisciplineActionLength(BigInteger.valueOf(rand.nextInt(100)));
        action.setDisciplineActionLengthDifferenceReason(GeneratorUtils
                .generateDisciplineActionLengthDifferenceReasonType());

        // construct and add the school references
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.setStateOrganizationId(responsibilitySchoolId);
        // edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(responsibilitySchoolId);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        action.setResponsibilitySchoolReference(schoolRef);

        // add Behaviors
        // ObjectFactory factory = new ObjectFactory();
        int count = 0;
        double prob = 1.0D / DisciplineDescriptor.values().length;
        for (DisciplineDescriptor behaviorDescriptor : DisciplineDescriptor.values()) {
            if (rand.nextDouble() < prob || count == (DisciplineDescriptor.values().length - 1)) {
                DisciplineDescriptorType disciplineDescriptorType = new DisciplineDescriptorType();
                // JAXBElement<String> disciplineDescriptorCode =
                // factory.createDisciplineDescriptorTypeCodeValue(behaviorDescriptor.codeValue);
                // JAXBElement<String> disciplineDescriptorShortDescription =
                // factory.createDisciplineDescriptorTypeShortDescription(behaviorDescriptor.shortDescription);
                // JAXBElement<String> disciplineDescriptorDescription =
                // factory.createDisciplineDescriptorTypeDescription(behaviorDescriptor.description);
                //
                //
                // disciplineDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(disciplineDescriptorCode);
                // disciplineDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(disciplineDescriptorShortDescription);
                // disciplineDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(disciplineDescriptorDescription);

                disciplineDescriptorType.setCodeValue(behaviorDescriptor.codeValue);
//                disciplineDescriptorType.setShortDescription(behaviorDescriptor.shortDescription);
//                disciplineDescriptorType.setDescription(behaviorDescriptor.description);
                action.getDisciplines().add(disciplineDescriptorType);
                break;
            }
            ++count;
        }

        return action;
    }

    // DisciplineDescriptor for DisciplineAction.
    public enum DisciplineDescriptor {
        REMOVAL("DISCIPLINE 001", "Discipline 001 description", "Removal from a class room description"), SUSPENSION(
                "DISCIPLINE 002", "Discipline 002 description", "Suspension from school description"), EXPULSION(
                "DISCIPLINE 003", "Discipline 003 description", "Expulsion description"), TRANSFER("DISCIPLINE 004",
                "Discipline 004 description", "Transfer to other school description");

        String codeValue;
        String shortDescription;
        String description;

        DisciplineDescriptor(String cv, String sd, String d) {
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

    public DisciplineAction generate(String disciplineId, String delimiter) {
        DisciplineAction disciplineAction = new DisciplineAction();

        try {
            Random random = new Random();

            String studentId = disciplineId.split(delimiter)[0];
            String schoolId = disciplineId.split(delimiter)[1];
            String discId = disciplineId.split(delimiter)[2];

            disciplineAction.setDisciplineActionIdentifier(discId);

            ObjectFactory fact = new ObjectFactory();
            DisciplineDescriptorType ddType = fact.createDisciplineDescriptorType();
            // JAXBElement<String> str =
            // fact.createDisciplineDescriptorTypeDescription("Suspension");
            // ddType.getCodeValueOrShortDescriptionOrDescription().add(str);
            ddType.setCodeValue("Suspension");
            ddType.setShortDescription("Suspension");
            ddType.setDescription("Suspension");
            disciplineAction.getDisciplines().add(ddType);

            disciplineAction.setDisciplineDate("2012-04-15");

            StudentReferenceType srt = StudentGenerator.getStudentReferenceType(studentId);
            disciplineAction.getStudentReference().add(srt);

            DisciplineIncidentReferenceType dirt = DisciplineGenerator.getDisciplineIncidentReferenceType(discId,
                    "ThisStateID");
            disciplineAction.getDisciplineIncidentReference().add(dirt);

            EducationalOrgReferenceType eor = SchoolGenerator.getEducationalOrgReferenceType(schoolId);
            disciplineAction.setResponsibilitySchoolReference(eor);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return disciplineAction;
    }
}
