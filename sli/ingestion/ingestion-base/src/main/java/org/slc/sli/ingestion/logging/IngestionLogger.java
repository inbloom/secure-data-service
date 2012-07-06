package org.slc.sli.ingestion.logging;

public interface IngestionLogger {

    public void debug(String msg);

    public void info(String msg);

    public void warn(String msg);

    public void debug(String msg, Object... params);

    public void info(String msg, Object... params);

    public void warn(String msg, Object... params);

    public void error(String msg, Object... params);

    public void error(String msg, Throwable x);

}
