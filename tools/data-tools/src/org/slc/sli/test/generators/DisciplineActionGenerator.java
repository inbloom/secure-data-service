package org.slc.sli.test.generators;

import java.util.Random;

import javax.xml.bind.JAXBElement;

import org.slc.sli.test.edfi.entities.*;

public class DisciplineActionGenerator {
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
          	JAXBElement<String> str = fact.createDisciplineDescriptorTypeDescription("Suspension");
          	ddType.getCodeValueOrShortDescriptionOrDescription().add(str);
          	disciplineAction.getDisciplines().add(ddType);
            
            disciplineAction.setDisciplineDate("2012-04-15");
            
            StudentReferenceType srt = StudentGenerator.getStudentReferenceType(studentId);
            disciplineAction.getStudentReference().add(srt);
            
            DisciplineIncidentReferenceType dirt = DisciplineGenerator.getDisciplineIncidentReferenceType(discId, "ThisStateID");
            disciplineAction.getDisciplineIncidentReference().add(dirt);
            
            EducationalOrgReferenceType eor = SchoolGenerator.getEducationalOrgReferenceType(schoolId);
            disciplineAction.setResponsibilitySchoolReference(eor);
              
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return disciplineAction;
    }

}
