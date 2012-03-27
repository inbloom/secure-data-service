package org.slc.sli.test.mappingGenerator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slc.sli.test.edfi.entities.*;
import org.slc.sli.test.generators.SchoolGenerator;

public class DataForASchool {
    private static List<String> schoolIds = new ArrayList<String>();
    private static List<SectionInternal> sections = new ArrayList<SectionInternal>();
    private static List<Teacher> teachers = new ArrayList<Teacher>();
    private static List<StaffEducationOrgEmploymentAssociationInternal> staffEducationOrgEmploymentAssociations = new ArrayList<StaffEducationOrgEmploymentAssociationInternal>();
    private static List<TeacherSectionAssociationInternal> teacherSectionAssociations = new ArrayList<TeacherSectionAssociationInternal>();
    
    
    /**
     * @param args
     * @throws JAXBException 
     */
    public static void main(String[] args) throws JAXBException {
        // generate schools
        schoolIds.add("School1");
        
        // generate sections
//        for (int i = 0; i < 10; i++) {
//            SectionInternal si = new SectionInternal();
//            
//        }
        printInterchangeEducationOrganization();
    }
    
    public static void printInterchangeEducationOrganization() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(InterchangeEducationOrganization.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
        
        InterchangeEducationOrganization interchangeEducationOrganization = new InterchangeEducationOrganization();
        
        for (String schoolId : schoolIds) {
            School school = SchoolGenerator.generate(schoolId);
            interchangeEducationOrganization.getStateEducationAgencyOrEducationServiceCenterOrFeederSchoolAssociation().add(school);
        }
        
        marshaller.marshal(interchangeEducationOrganization, System.out);
    }
    
    class SectionInternal {
        String uniqueSectionCode;
        int sequenceOfCourse;
    }
    
    class StaffEducationOrgEmploymentAssociationInternal {
        StaffReferenceType staffReference;
        EducationalOrgReferenceType educationOrganizationReference;
        StaffClassificationType staffClassification;
    }
    
    class TeacherSectionAssociationInternal {
        StaffReferenceType teacherReference;
        SectionReferenceType sectionReference;
        ClassroomPositionType classroomPosition;
    }
    
    class TeacherSchoolAssociationInternal {
        StaffReferenceType teacherReference;
        List<EducationalOrgReferenceType> schoolReference;
    }
    
}
