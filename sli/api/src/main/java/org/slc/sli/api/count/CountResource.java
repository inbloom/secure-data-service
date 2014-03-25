/*
 * Copyright 2012-2014 inBloom, Inc. and its affiliates.
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

package org.slc.sli.api.count;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.slc.sli.api.resources.generic.DefaultResource;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Resource that is a custom endpoint for getting counts. Currently this is set up for getting
 * data as it relates to the counts in the Databrowser.
 * 
 * @author mbrush
 *
 */

@Component
@Scope("request")
@Path("count")
@Produces({ HypermediaType.JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class CountResource extends DefaultResource {

	@Autowired
	CountService countService;

	@GET
	@Path("educationOrganizations")
	public @ResponseBody EducationOrganizationCount find(@Context HttpServletRequest request) {
		return countService.find();
	}

	@GET
	@Path("educationOrganizations/{edOrgId}")
	public @ResponseBody EducationOrganizationCount find(@PathParam("edOrgId") String edOrgId, @Context HttpServletRequest request) {
		return countService.findOne(edOrgId);
	}

	@GET
	@Path("teacherAssociations/{edOrgId}")
	public @ResponseBody TeacherAssociationCount findTeacherAssociations(@PathParam("edOrgId") String edOrgId, @Context HttpServletRequest request) {
		return countService.findTeacherAssociations(edOrgId);
	}

	@GET
	@Path("teacherAssociations/{edOrgId}/teachers")
	public @ResponseBody TeacherAssociationCount findTeachers(@PathParam("edOrgId") String edOrgId, @Context HttpServletRequest request) {
		return countService.findTeachers(edOrgId);
	}
}