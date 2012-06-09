package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
/**
 * Filters the entity by a given date
 *
 */
@Component
public class TeacherToStaffCohortAssociationEndDateFilter extends NodeDateFilter {


    static private final String GRACE_PERIOD = "0";

    @PostConstruct
    public void setParameters() {
        setParameters(EntityNames.STAFF_COHORT_ASSOCIATION, ParameterConstants.STAFF_ID, GRACE_PERIOD, ParameterConstants.END_DATE);
    }
}
