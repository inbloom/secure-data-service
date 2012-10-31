package org.slc.sli.search.process;


public interface Admin extends Process {
    /**
     * Delete the existing index and trigger extract/load/index for tenant.
     * @param tenantId - tenantId to reload for
     */
    public void reload(String tenantId);
    /**
     * Delete all and trigger extract/load/index
     */
    public void reloadAll();
    
    /**
     * Run updates for the existing index
     * @param tenantId - tenant to run for
     */
    public void reconcile(String tenantId);
    /**
     * Run updates for all existing indexes
     */
    public void reconcileAll();
}
