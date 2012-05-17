package org.slc.sli.ingestion.tenant;

import java.util.List;

/**
 * Interface for access to tenant data.
 *
 * @author jtully
 */
public interface TenantDA {
    List<String> getLzPaths(String ingestionServer);

    String getTenantId(String lzPath);

    void insertTenant(TenantRecord tenant);
}
