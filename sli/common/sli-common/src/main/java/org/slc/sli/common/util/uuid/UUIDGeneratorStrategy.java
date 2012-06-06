package org.slc.sli.common.util.uuid;

import java.util.UUID;

/**
 * Generates a UUID, provides a consistent strategy for _id fields in Mongo
 * @author smelody
 *
 */
public interface UUIDGeneratorStrategy {


    public String randomUUID();
}
