package org.slc.sli.view;

import org.slc.sli.config.Field;

/**
 * Basic interface to handle iterating through objects and counting them.
 */
public interface AggregateResolver {
    /**
     * Simple method that takes an xpath like expression to count the fields in each object.
     * @param configField a period delimited path in objects (EG: name.firstName)
     * @return a count of the occurrences.
     */
    public int getCountForPath(Field configField);
}
