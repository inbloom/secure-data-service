package org.slc.sli.client;

import java.util.List;

import org.slc.sli.entity.GenericEntity;

/**
 * 
 * An interface to the SLI API. 
 * This is meant to be a thin wrapper around API calls. It does group together multiple calls
 * in some cases, in a way useful to the rest of the dashboard application.
 *
 */
public interface APIClient {

    public List<GenericEntity> getSchools(final String token, List<String> schoolIds);
    public List<GenericEntity> getStudents(final String token, List<String> studentIds);
    public List<GenericEntity> getStudentAssessments(final String token, String studentId);
    public List<GenericEntity> getAssessments(final String token, List<String> assessmentIds);
    public List<GenericEntity> getCustomData(final String token, String key);
    public List<GenericEntity> getPrograms(final String token, List<String> studentIds);
    public List<GenericEntity> getParentEducationalOrganizations(final String token, GenericEntity educationalOrganization);
    public List<GenericEntity> getAssociatedEducationalOrganizations(final String token, GenericEntity school);
}
