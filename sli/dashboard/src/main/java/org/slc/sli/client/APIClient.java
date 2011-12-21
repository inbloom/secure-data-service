package org.slc.sli.client;

import java.util.List;

import org.slc.sli.entity.School;
import org.slc.sli.entity.Student;
import org.slc.sli.entity.Assessment;
import org.slc.sli.entity.CustomData;
/**
 * 
 * An interface to the SLI API. 
 * Each method should correspond to a view in the "real" API.
 * Each method's signature should have the following pattern:
 *  Entity[] getEntitys(final String token, List<String> filter1, List<String> filter2, ... ),   
 *  filter1, filter2, etc... correspond to the parameters passed into the API view. 
 *
 */
public interface APIClient {

    public School[] getSchools(final String token);
    public Student[] getStudents(final String token, List<String> studentIds);
    public Assessment[] getAssessments(final String token, List<String> studentIds);
    public CustomData[] getCustomData(final String token, String key);
    
}
