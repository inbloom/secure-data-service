package org.slc.sli.client;

import java.util.List;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;
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

    public GenericEntity[] getSchools(final String token);
    public GenericEntity[] getStudents(final String token, List<String> studentIds);
    public GenericEntity[] getAssessments(final String token, List<String> studentIds);
    public GenericEntity[] getCustomData(final String token, String key);
    public AssessmentMetaData[] getAssessmentMetaData(final String token);
    public GenericEntity[] getStudentProgramAssociation(final String token, List<String> studentIds);
}
