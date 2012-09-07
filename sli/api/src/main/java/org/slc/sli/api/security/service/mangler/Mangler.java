package org.slc.sli.api.security.service.mangler;

import java.util.List;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

public abstract class Mangler {
    private NeutralCriteria securityCriteria;
    private NeutralQuery theQuery;
    public abstract NeutralQuery mangleQuery(NeutralQuery query, NeutralCriteria securityCriteria);
    public NeutralQuery mangleQuery() {return mangleQuery(theQuery, securityCriteria);}
    public abstract boolean respondsTo(String type);
    public void setSecurityCriteria(NeutralCriteria securityCriteria) {
        this.securityCriteria = securityCriteria;
    }
    public void setTheQuery(NeutralQuery theQuery) {
        this.theQuery = theQuery;
    }
    
    protected List<String> adjustIdListForPaging(List<String> securedIds) {
        //There're fewer IDs than we have limited ourselves by, so don't worry.
        if (securedIds.size() <= theQuery.getLimit()) {
            debug("We aren't paging the security criteria because there is less security than limit");
            return securedIds;
        }
        //They want it all, so we give it to them.
        else if (theQuery.getLimit() == 0) {
            debug("We aren't paging the security criteria because of a limit of 0");
            return securedIds;
        }
        else {
            return securedIds.subList(theQuery.getOffset(), theQuery.getOffset() + theQuery.getLimit());
        }
    }
    
    @SuppressWarnings("unchecked")
    protected void adjustSecurityForPaging() {
        List<String> fullIds = (List <String>) securityCriteria.getValue();
        fullIds = adjustIdListForPaging(fullIds);
        securityCriteria.setValue(fullIds);
    }
    
    
    
}
