package org.slc.sli.common.util.logging;

/**
 * Types of log levels
 *
 * @author dshaw
 *
 */
public enum LogLevelType {
    TYPE_ALL("ALL"),
    TYPE_DEBUG("DEBUG"),
    TYPE_WARN("WARN"),
    TYPE_ERROR("ERROR"),
    TYPE_INFO("INFO"),
    TYPE_TRACE("TRACE");

    private String name;

    LogLevelType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
