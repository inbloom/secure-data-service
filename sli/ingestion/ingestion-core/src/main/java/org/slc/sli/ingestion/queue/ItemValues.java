package org.slc.sli.ingestion.queue;

/**
 *
 * Known values that a worker item can specify. This class exists for convenience and minimal
 * type-safety, nothing is enforcing these keypairs.
 * Using a class of string literals, since it is more concise than Java enum.
 *
 * @author smelody
 *
 */
public class ItemValues {
    public static final String UNCLAIMED = "unclaimed";
    public static final String WORKING = "working";
}
