package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Filters the entity by a given date
 *
 * @author pghosh
 *
 */
@Component
public class StaffEdOrgEdOrgIDNodeFilter extends NodeDateFilter{

    private static final String END_DATE = "endDate";
    private static final String ED_ORG_REF = "educationOrganizationReference";
    private static final String ZERO = "0";


    @Override
    public List<String> filterIds(List<String> toResolve) {

        setParameters(EntityNames.STAFF_ED_ORG_ASSOCIATION,ED_ORG_REF, ZERO,END_DATE);
        List<String> edOrgIdSet = super.filterIds(toResolve);

        setParameters(EntityNames.TEACHER_SCHOOL_ASSOCIATION,ParameterConstants.SCHOOL_ID, ZERO,END_DATE);
        edOrgIdSet.addAll(super.filterIds(toResolve));
        return edOrgIdSet;
    }
}


