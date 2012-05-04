package org.slc.sli.ingestion.routes;

/**
 * We view ingestion as a symphony, with a maestro conducting work and worker nodes in the pit (orchestra).
 *
 * @author smelody
 *
 */
public class IngestionNodeType {

    /** Maestro has the execution vision for the job */
    public static final String MAESTRO = "maestro";

    /** Life in the pit is execution of what the maestro tells you to do */
    public static final Object PIT = "pit";
}
