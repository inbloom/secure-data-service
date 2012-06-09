package org.slc.sli.api.security.context.resolver;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Filters edorg/school id based on  end date present in
 * StaffEgorgAssociation or teacherSchoolAssociation
 *
 * @author pghosh
 *
 */
@Component
public class StaffEdOrgEdOrgIDNodeFilter extends NodeDateFilter {

    private static final String END_DATE = "endDate";
    private static final String ZERO = "0";


    @PostConstruct
    public void setParameters() {
        setParameters(ZERO, END_DATE);
    }
}


