package org.slc.sli.test.generators;

import java.util.Random;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.BehaviorDescriptorType;
import org.slc.sli.test.edfi.entities.DisciplineIncident;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.SecondaryBehavior;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.WeaponsType;

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

//   /**
//    * Generates a list of StudentCohortAssociation from a CohortMeta.
//    *
//    * @param cohortMeta
//    * 
//    * @return <code>List<StudentCohortAssociation></code>
//    */
//   public static List<StudentCohortAssociation> generateLowFi(CohortMeta cohortMeta) {
//       String cohortId = cohortMeta.id;
//       String schoolId = cohortMeta.programMeta==null ? cohortMeta.schoolMeta.id : cohortMeta.programMeta.schoolId;
//       Set<String> studentIds = cohortMeta.studentIds;
//       
//       List<StudentCohortAssociation> list = new ArrayList<StudentCohortAssociation>(studentIds.size());
//       
//       for (String studentId : studentIds) {
//           list.add(generateLowFi(cohortId, studentId, schoolId));
//       }
//
//       return list;
//   }

   /**
    * Generates a DisciplineIncident between a cohort and a student 
    * with a school as a reference.
    *
    * @param cohortId
    * @param studentId
    * @param schoolId
    * 
    * @return <code>DisciplineIncident</code>
    */
   public static DisciplineIncident generateLowFi(String disciplineIncidentId, String schoolId, String staffId) {

       DisciplineIncident incident = new DisciplineIncident();
       
       incident.setIncidentIdentifier(disciplineIncidentId);
       //set incident date and time
       incident.setIncidentDate(date);
       incident.setIncidentTime(time);
       incident.setIncidentLocation(GeneratorUtils.generateIncidentLocationType());
       incident.setReporterDescription(GeneratorUtils.generateReporterDescriptionType());
       incident.setReporterName("Discipline Incident Reporter-"+disciplineIncidentId);
       WeaponsType wt = new WeaponsType();
       wt.getWeapon().add(GeneratorUtils.generateWeaponItemType());
       incident.setWeapons(wt);
       incident.setReportedToLawEnforcement(new Boolean(rand.nextBoolean()));
       incident.setCaseNumber("CaseNumber-"+rand.nextInt());
       
       //add Behaviors
       ObjectFactory factory = new ObjectFactory();
       double prob = 1.0D / BehaviorDescriptor.values().length;
       for(BehaviorDescriptor behaviorDescriptor : BehaviorDescriptor.values()) {
           if (rand.nextDouble() < prob) {
               BehaviorDescriptorType behaviorDescriptorType = new BehaviorDescriptorType();
               JAXBElement<String> behaviorDescriptorCode =  factory.createBehaviorDescriptorTypeCodeValue(behaviorDescriptor.codeValue);
               JAXBElement<String> behaviorDescriptorShortDescription =  factory.createBehaviorDescriptorTypeShortDescription(behaviorDescriptor.shortDescription);
               JAXBElement<String> behaviorDescriptorDescription =  factory.createBehaviorDescriptorTypeDescription(behaviorDescriptor.description);
               behaviorDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(behaviorDescriptorCode);
               behaviorDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(behaviorDescriptorShortDescription);
               behaviorDescriptorType.getCodeValueOrShortDescriptionOrDescription().add(behaviorDescriptorDescription);
               incident.getBehaviors().add(behaviorDescriptorType);
           }
       }
       
       //add SecondaryBehavior
       SecondaryBehavior sb = new SecondaryBehavior();
       sb.setSecondaryBehavior("SecondaryBehavior-"+rand.nextInt());
       sb.setBehaviorCategory(GeneratorUtils.generateBehaviorCategoryType());
       incident.getSecondaryBehaviors().add(sb);

       // construct and add the school references
       EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
       edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
       EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
       schoolRef.setEducationalOrgIdentity(edOrgIdentity);
       incident.setSchoolReference(schoolRef);

       // construct and add the staff reference
       StaffIdentityType sit = new StaffIdentityType();
       sit.setStaffUniqueStateId(staffId);
       StaffReferenceType srt = new StaffReferenceType();
       srt.setStaffIdentity(sit);
       incident.setStaffReference(srt);
       
       return incident;
   }

   // BehaviorDescriptor for DisciplineIncident. 
   public enum BehaviorDescriptor {
       MINOR("BEHAVIOR 001", "Behavior 001 description", "Minor behavior description"),
       BULLY("BEHAVIOR 002", "Behavior 002 description", "Bully behavior description"),
       ORAL("BEHAVIOR 003", "Behavior 003 description", "Oral behavior description"),
       VIOLENT("BEHAVIOR 004", "Behavior 004 description", "Violent behavior description");
       
       String codeValue;
       String shortDescription;
       String description;
       
       BehaviorDescriptor (String cv, String sd, String d) {
           codeValue = cv;
           shortDescription = sd;
           description = d;
       }
       public String getCodeValue() { return codeValue; }
       public String getShortDescription() { return shortDescription; }
       public String getDescription() { return description; }
   }
}
