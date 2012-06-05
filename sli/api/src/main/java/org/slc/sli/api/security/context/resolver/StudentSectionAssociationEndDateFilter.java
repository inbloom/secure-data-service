package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Filters the entity by a given date
 *
 * @author pghosh
 *
 */
@Component
public class StudentSectionAssociationEndDateFilter extends NodeDateFilter{

    private static final String END_DATE = "endDate";

    @Value("${sli.security.gracePeriod}")
    private String gracePeriodVal;

    @PostConstruct
    public void setParameters (){
        setParameters(EntityNames.STUDENT_SECTION_ASSOCIATION,ParameterConstants.STUDENT_ID, gracePeriodVal,END_DATE);
    }
}
