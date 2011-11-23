package org.sli.ingestion;

import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.repository.SchoolRepository;
import org.slc.sli.repository.StudentRepository;

import org.sli.ingestion.processors.ContextManager;
import org.springframework.stereotype.Component;


/**
 * Interchange StudentSchoolAssociation domain object.
 * 
 * Necessary for marshaling Interchange representations, and their ED-FI specific identifiers, to the SLI object domain
 * as well as persisting using the appropriate DAL repository.
 * 
 */

@Component
public class StudentSchoolAssociationInterchange extends StudentSchoolAssociation implements InterchangeAssociation {
	
    private String studentUniqueStateId;
    private String stateOrganizationId;
    private StudentSchoolAssociation association = new StudentSchoolAssociation();
    
    public StudentSchoolAssociationInterchange() {
    	super();
    }
    
    /**
     * @return the studentUniqueStateId
     */
    public String getStudentUniqueStateId() {
        return studentUniqueStateId;
    }

    /**
     * @param studentUniqueStateId the studentUniqueStateId to set
     */
    public void setStudentUniqueStateId(String studentUniqueStateId) {
        this.studentUniqueStateId = studentUniqueStateId;
    }

    /**
     * @return the stateOrganizationId
     */
    public String getStateOrganizationId() {
        return stateOrganizationId;
    }

    /**
     * @param stateOrganizationId the stateOrganizationId to set
     */
    public void setStateOrganizationId(String stateOrganizationId) {
        this.stateOrganizationId = stateOrganizationId;
    }
    
    /**
     * @return the domain association
     */
    public StudentSchoolAssociation getAssociation() {
        return association;
    }

    /**
     * @param association the association to set
     */
    public void setAssociation(StudentSchoolAssociation association) {
        this.association = association;
    }
    
	/**
	 * Initialize the SLI Ingestion association instance by looking up the associated SLI instances according to the ED-FI identifiers
	 * 
     * @param contextManager
	 * 
	 */
	public void init(ContextManager contextManager) {
		
    	// TODO - Request DAL to be enhanced to allow Association objects to be persisted by specifying ED-FI specific identifiers only and NOT SLI identifiers.

    	// Lookup associated Student using ED-FI identifier
		StudentRepository studentRepository = (StudentRepository)contextManager.getRepository(Student.class.getName());
    	Student student = null;
    	Iterable studentIterable = studentRepository.findByStudentSchoolId(this.getStudentUniqueStateId());
    	if (studentIterable.iterator().hasNext()) {
        	student = (Student)studentIterable.iterator().next();
    	}
    	
    	// Lookup associated School using ED-FI identifier
    	School school = null;
    	SchoolRepository schoolRepository = (SchoolRepository)contextManager.getRepository(School.class.getName());
    	Iterable schoolIterable = schoolRepository.findByStateOrganizationId(this.getStateOrganizationId());
    	if (schoolIterable.iterator().hasNext()) {
        	school = (School)schoolIterable.iterator().next();
    	}
    	
    	if ((student != null) && (school != null)) {   		
    		this.getAssociation().setStudentId(student.getStudentId());
    		this.getAssociation().setSchoolId(school.getSchoolId());
    	}
    	else {
    		throw new IllegalArgumentException("StudentSchoolAssociation initialization failed.");
    	}
	}
	
}
