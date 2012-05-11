package org.slc.sli.common.util.uuid;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Generates Type 4 (random) UUIDs.
 *
 * @author smelody
 *
 */
@Component
public class Type4UUIDGeneratorStrategy implements UUIDGeneratorStrategy {

    /**
     * Generate a type 4 random UUID.
     */
    @Override
    public UUID randomUUID() {
        return UUID.randomUUID();
    }

}
