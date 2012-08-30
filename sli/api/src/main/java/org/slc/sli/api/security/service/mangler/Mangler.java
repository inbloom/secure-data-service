package org.slc.sli.api.security.service.mangler;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

public interface Mangler {
    public NeutralQuery mangleQuery(NeutralQuery query, NeutralCriteria securityCriteria);
    public boolean respondsTo(String type);
}
