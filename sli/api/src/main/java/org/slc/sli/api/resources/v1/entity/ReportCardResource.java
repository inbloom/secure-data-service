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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.v1.DefaultCrudResource;

/**
 * Represents the definition of a report card resource. A report card is an educational entity that
 * represents the collection of student grades for courses taken during a grading period.
 *
 * For detailed information, see the schema for $$ReportCard$$ resources.
 *
 * @author chung
 */
@Path(PathConstants.V1 + "/" + PathConstants.REPORT_CARDS)
@Component
@Scope("request")
public class ReportCardResource extends DefaultCrudResource {

    @Autowired
    public ReportCardResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.REPORT_CARDS);
    }

}
