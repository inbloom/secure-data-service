package org.slc.sli.api.security.resolve;

import org.slc.sli.api.security.SLIPrincipal;

/**
 *
 * @author dkornishev
 *
 */
public interface UserLocator {

    public SLIPrincipal locate(String realm, String externalUserId);

}
