package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *Filters staffId/teacherid based on  end date present in
 * StaffEgorgAssociation or teacherSchoolAssociation
 *
 * @author pghosh
 *
 */
@Component
public class StaffEdOrgStaffIDNodeFilter extends NodeDateFilter {

    private static final String END_DATE = "endDate";
    private static final String STAFF_REF = "staffReference";
    private static final String ZERO = "0";

    @Override
    public List<String> filterIds(List<String> toResolve) {

        setParameters(EntityNames.STAFF_ED_ORG_ASSOCIATION, STAFF_REF, ZERO, END_DATE);
        List<String> edOrgIdSet = super.filterIds(toResolve);

        setParameters(EntityNames.TEACHER_SCHOOL_ASSOCIATION, ParameterConstants.TEACHER_ID, ZERO, END_DATE);
        edOrgIdSet.addAll(super.filterIds(toResolve));
        return edOrgIdSet;
    }
}

