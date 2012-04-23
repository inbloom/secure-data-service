package org.slc.sli.common.util.uuid;

import java.util.UUID;

/**
 * Generates a UUID, provides a consistent strategy for _id fields in Mongo
 * @author smelody
 *
 */
public interface UUIDGeneratorStrategy {

    /** Generate a random UUID, according to this strategy's preferred method of of generating UUIDs. */
    public UUID randomUUID();
}
