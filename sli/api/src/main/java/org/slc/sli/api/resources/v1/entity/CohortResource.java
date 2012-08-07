/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;

/**
 * Represents the definition of a cohort.
 *
 * A cohort is defined as a list of
 * designated students for tracking, analysis, or intervention.
 *
 * For detailed information, see the schema for $$Cohort$$ resources.
 *
 * @author jstokes
 * @author srichards
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.COHORTS)
@Component
@Scope("request")
public class CohortResource extends DefaultCrudResource {

    public static final String COHORT_IDENTIFIER = "cohortIdentifier";
    public static final String COHORT_DESCRIPTION = "cohortDescription";
    public static final String COHORT_TYPE = "cohortType";
    public static final String COHORT_SCOPE = "cohortScope";
    public static final String ACADEMIC_SUBJECT = "academicSubject";
    public static final String EDUCATION_ORGANIZATION_ID = "educationOrgId";

    @Autowired
    public CohortResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.COHORTS);
    }

   /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + COHORT_IDENTIFIER + "}" + "/" + PathConstants.STAFF_COHORT_ASSOCIATIONS)
    public Response getStaffCohortAssociations(@PathParam(COHORT_IDENTIFIER) final String cohortId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_COHORT_ASSOCIATIONS, ParameterConstants.COHORT_ID, cohortId, headers, uriInfo);
    }


    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + COHORT_IDENTIFIER + "}" + "/" + PathConstants.STAFF_COHORT_ASSOCIATIONS + "/" + PathConstants.STAFF)
    public Response getStaffCohortAssociationStaff(@PathParam(COHORT_IDENTIFIER) final String cohortId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STAFF_COHORT_ASSOCIATIONS, ParameterConstants.COHORT_ID, cohortId,
                ParameterConstants.STAFF_ID, ResourceNames.STAFF, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + COHORT_IDENTIFIER + "}" + "/" + PathConstants.STUDENT_COHORT_ASSOCIATIONS)
    public Response getStudentCohortAssociations(@PathParam(COHORT_IDENTIFIER) final String cohortId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, ParameterConstants.COHORT_ID, cohortId, headers, uriInfo);
    }


    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + COHORT_IDENTIFIER + "}" + "/" + PathConstants.STUDENT_COHORT_ASSOCIATIONS + "/" + PathConstants.STUDENTS)
    public Response getStudentCohortAssociationStudents(@PathParam(COHORT_IDENTIFIER) final String cohortId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, ParameterConstants.COHORT_ID, cohortId,
                ParameterConstants.STUDENT_ID, ResourceNames.STUDENTS, headers, uriInfo);
    }
}
