package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.slc.sli.test.edfi.entities.DisciplineIncident;
import org.slc.sli.test.edfi.entities.DisciplineIncidentReferenceType;
import org.slc.sli.test.edfi.entities.ReferenceType;
import org.slc.sli.test.edfi.entities.StudentDisciplineIncidentAssociation;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentParticipationCodeType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.meta.DisciplineIncidentMeta;

/**
* Generates StudentDisciplineIncidentAssociation data
* 
* @author slee
*
*/
public class StudentDisciplineAssociationGenerator {

    /**
     * Generates a list of StudentDisciplineIncidentAssociation from a DisciplineIncidentMeta.
     *
     * @param meta
     * 
     * @return <code>List<StudentDisciplineIncidentAssociation></code>
     */
    public static List<StudentDisciplineIncidentAssociation> generateLowFi(DisciplineIncidentMeta meta) {
        String disciplineIncidentId = meta.id;
        Collection<String> studentIds = meta.studentIds;
        
        List<StudentDisciplineIncidentAssociation> list = new ArrayList<StudentDisciplineIncidentAssociation>(studentIds.size());
        
        for (String studentId : studentIds) {
            list.add(generateLowFi(studentId, disciplineIncidentId));
        }

        return list;
    }

    /**
     * Generates a StudentDisciplineIncidentAssociation between a student and a disciplineIncident.
     *
     * @param studentId
     * @param disciplineIncidentId
     * 
     * @return <code>StudentDisciplineIncidentAssociation</code>
     */
    public static StudentDisciplineIncidentAssociation generateLowFi(String studentId, String disciplineIncidentId) {

        StudentDisciplineIncidentAssociation assoc = new StudentDisciplineIncidentAssociation();
        
        assoc.setStudentParticipationCode(GeneratorUtils.generateStudentParticipationCodeType());

        // construct and add the student reference
        StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        assoc.setStudentReference(srt);
        
        // construct and add the disciplineIncident Reference       
        ReferenceType dir = new ReferenceType();
        dir.setId(disciplineIncidentId);
        assoc.setDisciplineIncidentReference(dir);

        return assoc;
    }
    
    /**
     * Generates a StudentDisciplineIncidentAssociation between a student and a disciplineIncident.
     *
     * @param studentId
     * @param disciplineIncident
     * 
     * @return <code>StudentDisciplineIncidentAssociation</code>
     */
    public static StudentDisciplineIncidentAssociation generateLowFi(String studentId, DisciplineIncident disciplineIncident) {

        StudentDisciplineIncidentAssociation assoc = new StudentDisciplineIncidentAssociation();
        
        assoc.setStudentParticipationCode(GeneratorUtils.generateStudentParticipationCodeType());

        // construct and add the student reference
        StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        assoc.setStudentReference(srt);
        
        // construct and add the disciplineIncident Reference       
        ReferenceType dir = new ReferenceType();
        dir.setRef(disciplineIncident);
        dir.setId(disciplineIncident.getIncidentIdentifier());
        assoc.setDisciplineIncidentReference(dir);

        return assoc;
    }

    public StudentDisciplineIncidentAssociation generate(String studentDisciplineId, String delimiter) {
    	StudentDisciplineIncidentAssociation studentDisciplineAssociation = new StudentDisciplineIncidentAssociation();

    	try {
            Random random = new Random();
            
            String studentId = studentDisciplineId.split(delimiter)[0];
            String discId = studentDisciplineId.split(delimiter)[2];
            
            StudentReferenceType srt = StudentGenerator.getStudentReferenceType(studentId);
            studentDisciplineAssociation.setStudentReference(srt);

            DisciplineIncidentReferenceType dirt = DisciplineGenerator.getDisciplineIncidentReferenceType(discId, "ThisStateID");
            studentDisciplineAssociation.setDisciplineIncidentReference(dirt);
            
            StudentParticipationCodeType spcType = null;
            int randInt4 = random.nextInt(4);
                 if (randInt4 == 0) spcType = StudentParticipationCodeType.PERPETRATOR;
            else if (randInt4 == 1) spcType = StudentParticipationCodeType.REPORTER;
            else if (randInt4 == 2) spcType = StudentParticipationCodeType.VICTIM;
            else if (randInt4 == 3) spcType = StudentParticipationCodeType.WITNESS;
            studentDisciplineAssociation.setStudentParticipationCode(spcType);
            
            // Behaviors (optional)
            
            // Secondary Behaviors (optional)
              
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return studentDisciplineAssociation;
    }

	
}
