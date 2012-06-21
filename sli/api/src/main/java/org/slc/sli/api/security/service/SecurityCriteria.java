package org.slc.sli.api.security.service;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

/**
 * Encapsulates security criteria used by queries
 *
 * @author srupasinghe
 *
 */
public class SecurityCriteria {
    //main security criteria
    private NeutralCriteria securityCriteria;
    //black list criteria
    private NeutralCriteria blacklistCriteria;

    public NeutralCriteria getSecurityCriteria() {
        return securityCriteria;
    }

    public NeutralCriteria getBlacklistCriteria() {
        return blacklistCriteria;
    }

    public void setSecurityCriteria(NeutralCriteria securityCriteria) {
        this.securityCriteria = securityCriteria;
    }

    public void setBlacklistCriteria(NeutralCriteria blacklistCriteria) {
        this.blacklistCriteria = blacklistCriteria;
    }

    /**
     * Apply the security criteria to the given query
     * @param query The query to manipulate
     * @return
     */
    public NeutralQuery applySecurityCriteria(NeutralQuery query) {
        query.addCriteria(securityCriteria);

        if (blacklistCriteria != null) {
            query.addCriteria(blacklistCriteria);
        }

        return query;
    }
}
