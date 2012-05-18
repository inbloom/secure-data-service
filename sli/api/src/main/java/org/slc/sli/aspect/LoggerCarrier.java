package org.slc.sli.aspect;

import org.slc.sli.common.util.logging.SecurityEvent;

/**
 * Logging "super class" injected into all sli classes via aspectj
 * inter-type declaration.  Java doesn't have mixins :(
 *
 * @author dkornishev
 *
 */
public interface LoggerCarrier {
    public void debug(String msg);
    public void info(String msg);
    public void warn(String msg);
    public void debug(String msg, Object... params);
    public void info(String msg, Object... params);
    public void warn(String msg, Object... params);
    public void error(String msg, Throwable x);
    public void audit(SecurityEvent event);
}
