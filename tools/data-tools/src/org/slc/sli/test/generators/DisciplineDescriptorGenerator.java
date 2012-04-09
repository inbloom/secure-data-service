package org.slc.sli.test.generators;

import org.slc.sli.test.edfi.entities.*;

public class DisciplineDescriptorGenerator {
    public DisciplineDescriptor generate(int iBehavior, String schoolId) {
    	DisciplineDescriptor disciplineDescriptor = new DisciplineDescriptor();

    	try {
            EducationalOrgReferenceType eor = SchoolGenerator.getEducationalOrgReferenceType(schoolId);
            switch (iBehavior) {
            case 0:
            	disciplineDescriptor.setCodeValue("DET");
            	disciplineDescriptor.setShortDescription("Detention");
            	disciplineDescriptor.getEducationOrganizationReference().add(eor);
            	break;
            case 1:
            	disciplineDescriptor.setCodeValue("SUS");
            	disciplineDescriptor.setShortDescription("Suspension");
            	disciplineDescriptor.getEducationOrganizationReference().add(eor);
            	break;
            case 2:
            	disciplineDescriptor.setCodeValue("EXP");
            	disciplineDescriptor.setShortDescription("Expulsion");
            	disciplineDescriptor.getEducationOrganizationReference().add(eor);
            	break;
            default:
            	disciplineDescriptor.setCodeValue("ISS");
            	disciplineDescriptor.setShortDescription("In-School Suspension");
            	disciplineDescriptor.getEducationOrganizationReference().add(eor);
            	break;
            }
              
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return disciplineDescriptor;
    }


}
