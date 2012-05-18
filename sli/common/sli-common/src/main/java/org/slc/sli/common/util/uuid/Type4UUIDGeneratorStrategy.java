package org.slc.sli.common.util.uuid;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Generates Type 4 (random) UUIDs.
 *
 * @author smelody
 *
 */
@Component
@Qualifier("type4UUIDGeneratorStrategy")
public class Type4UUIDGeneratorStrategy implements UUIDGeneratorStrategy {

    /**
     * Generate a type 4 random UUID.
     */
    @Override
    public UUID randomUUID() {
        return UUID.randomUUID();
    }

}
