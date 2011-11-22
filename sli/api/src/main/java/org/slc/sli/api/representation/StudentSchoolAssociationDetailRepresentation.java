package org.slc.sli.api.representation;

import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import com.sun.jersey.server.linking.Link;
import com.sun.jersey.server.linking.Links;
import com.sun.jersey.server.linking.Ref;
import com.sun.jersey.server.linking.Ref.Style;

import org.slc.sli.domain.StudentSchoolAssociation;

/**
 * Decorates a StudentSchoolAssociation with links to the Student and School
 * resource.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */

// The links defined in the header annotation provide links to the student and
// school on either ends of the association.
@XmlRootElement(name = "StudentSchoolAssociation")
@Links({ @Link(value = @Ref(style = Style.ABSOLUTE, value = "student/{studentId}"), rel = "student"),
        @Link(value = @Ref(style = Style.ABSOLUTE, value = "schools/{schoolId}"), rel = "school") })
public class StudentSchoolAssociationDetailRepresentation {
    
    private int studentId;
    private int schoolId;
    private Collection<StudentSchoolAssociation> associations;
    
    /**
     * Construct a new representation of these associations.
     * 
     * @param association
     */
    public StudentSchoolAssociationDetailRepresentation(Collection<StudentSchoolAssociation> associations) {
        this.associations = associations;
    }
    
    /**
     * @return the studentId
     */
    public int getStudentId() {
        return studentId;
    }
    
    /**
     * @param studentId
     *            the studentId to set
     */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    /**
     * @return the schoolId
     */
    public int getSchoolId() {
        return schoolId;
    }
    
    /**
     * @param schoolId
     *            the schoolId to set
     */
    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }
    
}
