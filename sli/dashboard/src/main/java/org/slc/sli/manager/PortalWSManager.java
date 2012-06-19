package org.slc.sli.manager;

/**
 * Retrieves header and footer from Portal WS
 *
 * @author svankina
 *
 */
public interface PortalWSManager {

    public String getHeader(boolean isAdmin);
    public String getFooter(boolean isAdmin);
}
