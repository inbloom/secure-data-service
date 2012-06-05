package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.ParentReferenceType;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.RelationType;
import org.slc.sli.test.edfi.entities.StudentParentAssociation;
import org.slc.sli.test.edfi.entities.StudentReferenceType;

public class StudentParentAssociationGenerator {
   public static Random random = new Random();

    public StudentParentAssociation generate(String studentParentId, String studentId) {

    	StudentParentAssociation studentParentAssociation = new StudentParentAssociation();

    	try {

            StudentReferenceType srt = StudentGenerator.getStudentReferenceType(studentId);

            studentParentAssociation.setStudentReference(srt);

            studentParentAssociation.setRelation(random.nextBoolean() ? RelationType.MOTHER : RelationType.FATHER);

            boolean bool = random.nextBoolean();

            studentParentAssociation.setPrimaryContactStatus(bool);

            studentParentAssociation.setLivesWith(bool);

            studentParentAssociation.setEmergencyContactStatus(bool);

            studentParentAssociation.setContactPriority(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentParentAssociation;
    }



      public static StudentParentAssociation generateLowFi(String parentId, boolean isMale, String studentId) {
        StudentParentAssociation studentParentAssociation = new StudentParentAssociation();

        try {
        	
        	Ref parentRef = new Ref (parentId);
        	ParentReferenceType prt = new ParentReferenceType();
        	prt.setRef(parentRef);
        	studentParentAssociation.setParentReference(prt);
        	
        	Ref studentRef = new Ref (studentId);
        	StudentReferenceType srt = new StudentReferenceType();
        	srt.setRef(studentRef);
        	studentParentAssociation.setStudentReference(srt);
        	
        	/* original
            StudentReferenceType srt = StudentGenerator.getStudentReferenceType(studentId);
            studentParentAssociation.setStudentReference(srt);

            ParentReferenceType prt = ParentGenerator.getParentReferenceType(parentId);
        	
            studentParentAssociation.setParentReference(prt);
            */
            if(isMale)
                studentParentAssociation.setRelation(RelationType.FATHER);
            else
                studentParentAssociation.setRelation(RelationType.MOTHER);

            boolean bool = random.nextBoolean();

            studentParentAssociation.setPrimaryContactStatus(bool);

            studentParentAssociation.setLivesWith(bool);

            studentParentAssociation.setEmergencyContactStatus(bool);

            studentParentAssociation.setContactPriority(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentParentAssociation;
    }
}
