package org.slc.sli.api.security.service;

import org.slc.sli.common.util.logging.SecurityEvent;

/**
 * Audit logging service interface
 *
 * This replaces what was once implemented in the LoggerCarrierAspect.
 */
public interface AuditLogger {

    // only goes to the audit log file
    void auditLog(SecurityEvent event);

    // goes to both the audit log file and the database
    void audit(SecurityEvent event);
}
