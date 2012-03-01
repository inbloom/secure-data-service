package org.slc.sli.view;

/**
 * Basic interface to handle iterating through objects and counting them.
 */
public interface AggregateResolver {
    /**
     * Simple method that takes an xpath like expression to count the fields in each object.
     * @param path a period delimited path in objects (EG: name.firstName)
     * @return a count of the occurrences.
     */
    public int getCountForPath(String path);
}
