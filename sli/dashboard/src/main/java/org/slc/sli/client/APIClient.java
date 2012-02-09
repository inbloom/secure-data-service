package org.slc.sli.client;

import java.util.List;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.assessmentmetadata.AssessmentMetaData;

/**
 * 
 * An interface to the SLI API. 
 * Ideally, each method should correspond to a call to the "real" API.
 *
 */
public interface APIClient {

    public List<GenericEntity> getSchools(final String token, List<String> schoolIds);
    public List<GenericEntity> getStudents(final String token, List<String> studentIds);
    public List<GenericEntity> getAssessments(final String token, List<String> studentIds);
    public List<GenericEntity> getCustomData(final String token, String key);
    public AssessmentMetaData[] getAssessmentMetaData(final String token);
    public List<GenericEntity> getPrograms(final String token, List<String> studentIds);
    public List<GenericEntity> getParentEducationalOrganizations(final String token, GenericEntity educationalOrganization);
    public List<GenericEntity> getAssociatedEducationalOrganizations(final String token, GenericEntity school);
}
