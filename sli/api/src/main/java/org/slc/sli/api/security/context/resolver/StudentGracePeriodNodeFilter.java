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
public class StudentGracePeriodNodeFilter extends NodeDateFilter {

    private static final String EXIT_WITHDRAW_DATE = "exitWithdrawDate";

    @Value("${sli.security.gracePeriod}")
    private String gracePeriodVal;

    @PostConstruct
    public void setParameters() {
        setParameters(EntityNames.STUDENT_SCHOOL_ASSOCIATION, ParameterConstants.STUDENT_ID, gracePeriodVal, EXIT_WITHDRAW_DATE);
    }
}

