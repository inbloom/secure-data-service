package org.slc.sli.search.process;

/**
 * Base for all process nodes
 *
 */
public interface Process {
    /**
     * Get human readable health of the process node
     * @return
     */
    public String getHealth();
}
