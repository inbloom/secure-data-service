package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.ParentReferenceType;
import org.slc.sli.test.edfi.entities.RelationType;
import org.slc.sli.test.edfi.entities.StudentParentAssociation;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.meta.ParentMeta;
import org.slc.sli.test.edfi.entities.meta.StudentMeta;

public class StudentParentAssociationGenerator {

    public StudentParentAssociation generate(String studentParentId, String studentId) {
    	StudentParentAssociation studentParentAssociation = new StudentParentAssociation();

    	try {
            Random random = new Random();

//            String studentId = studentParentId.split(delimiter)[0];
//            String parentId = studentParentId.split(delimiter)[1];
//
            StudentReferenceType srt = StudentGenerator.getStudentReferenceType(studentId);
            studentParentAssociation.setStudentReference(srt);

  //          ParentReferenceType prt = ParentGenerator.getParentReferenceType(parentId);
           // studentParentAssociation.setParentReference(prt);

            studentParentAssociation.setRelation(random.nextBoolean() ? RelationType.MOTHER : RelationType.FATHER);

            studentParentAssociation.setPrimaryContactStatus(true);

            studentParentAssociation.setLivesWith(true);

            studentParentAssociation.setEmergencyContactStatus(true);

            studentParentAssociation.setContactPriority(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentParentAssociation;
    }




    //public static StudentParentAssociation generateLowFi(ParentMeta parentMeta, StudentMeta studentMeta) {
      public static StudentParentAssociation generateLowFi(String parentId, boolean isMale, String studentId) {
        StudentParentAssociation studentParentAssociation = new StudentParentAssociation();

        try {
            Random random = new Random();

//            String studentId = studentParentId.split(delimiter)[0];
//            String parentId = studentParentId.split(delimiter)[1];
//
            StudentReferenceType srt = StudentGenerator.getStudentReferenceType(studentId);
            studentParentAssociation.setStudentReference(srt);

            ParentReferenceType prt = ParentGenerator.getParentReferenceType(parentId);

            studentParentAssociation.setParentReference(prt);
            if(isMale)
                studentParentAssociation.setRelation(RelationType.FATHER);
            else
                studentParentAssociation.setRelation(RelationType.MOTHER);
            //studentParentAssociation.setRelation(random.nextBoolean() ? RelationType.MOTHER : RelationType.FATHER);

            studentParentAssociation.setPrimaryContactStatus(true);

            studentParentAssociation.setLivesWith(true);

            studentParentAssociation.setEmergencyContactStatus(true);

            studentParentAssociation.setContactPriority(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentParentAssociation;
    }
}
