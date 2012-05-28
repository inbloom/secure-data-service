package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.Path;

import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.PathConstants;

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
