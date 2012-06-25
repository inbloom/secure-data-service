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

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.slc.sli.api.resources.v1.HypermediaType;

/**
 * Represents the cumulative record of academic achievements and the collection of student grades
 * for the student at the end of a semester or school year.
 *
 * If you're looking for records for a particular course, use StudentTranscriptAssociationResource
 * instead.
 *
 * For detailed information, see the schema for $$StudentAcademicRecord$$ resources.
 *
 * @author kmyers
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENT_ACADEMIC_RECORDS)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8", HypermediaType.VENDOR_SLC_JSON + ";charset=utf-8" })
public class StudentAcademicRecordResource extends DefaultCrudResource {

    @Autowired
    public StudentAcademicRecordResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENT_ACADEMIC_RECORDS);
        debug("Initialized a new {}", StudentAcademicRecordResource.class);
    }

}
