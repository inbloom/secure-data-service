package org.slc.sli.search.process;


public interface Admin extends Process {
    /**
     * Delete the existing index and trigger extract/load/index for tenant.
     * @param tenantId - tenantId to reload for
     */
    void reload(String tenantId);
    /**
     * Delete all and trigger extract/load/index
     */
    void reloadAll();

    /**
     * Run updates for the existing index
     * @param tenantId - tenant to run for
     */
    void reconcile(String tenantId);
    /**
     * Run updates for all existing indexes
     */
    void reconcileAll();
}
