package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.*;

public class BehaviorDescriptorGenerator {
    public BehaviorDescriptor generate(int iBehavior, String schoolId) {
    	BehaviorDescriptor behaviorDescriptor = new BehaviorDescriptor();

    	try {
            EducationalOrgReferenceType eor = SchoolGenerator.getEducationalOrgReferenceType(schoolId);
            switch (iBehavior) {
            case 0:
            	behaviorDescriptor.setBehaviorCategory(BehaviorCategoryType.SCHOOL_CODE_OF_CONDUCT);
            	behaviorDescriptor.setCodeValue("PDA");
            	behaviorDescriptor.setShortDescription("Public Display of Affection");
            	behaviorDescriptor.getEducationOrganizationReference().add(eor);
            	break;
            case 1:
            	behaviorDescriptor.setBehaviorCategory(BehaviorCategoryType.SCHOOL_VIOLATION);
            	behaviorDescriptor.setCodeValue("BUL");
            	behaviorDescriptor.setShortDescription("Bullying");
            	behaviorDescriptor.getEducationOrganizationReference().add(eor);
            	break;
            case 2:
            	behaviorDescriptor.setBehaviorCategory(BehaviorCategoryType.STATE_LAW_CRIME);
            	behaviorDescriptor.setCodeValue("PCS");
            	behaviorDescriptor.setShortDescription("Possession of Controlled Substance");
            	behaviorDescriptor.getEducationOrganizationReference().add(eor);
            	break;
            default:
            	behaviorDescriptor.setBehaviorCategory(BehaviorCategoryType.STATE_OFFENSE);
            	behaviorDescriptor.setCodeValue("PFA");
            	behaviorDescriptor.setShortDescription("Possession of Firearm");
            	behaviorDescriptor.getEducationOrganizationReference().add(eor);
            	break;
            }
              
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return behaviorDescriptor;
    }


}
