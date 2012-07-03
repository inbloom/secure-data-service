package org.slc.sli.sif.reporting;

import java.util.Properties;

import openadk.library.Event;

public interface EventGenerator {

    public static final String MESSAGE_FILE = "MESSAGE_FILE";

    public Event generateEvent(Properties eventProps);
}
