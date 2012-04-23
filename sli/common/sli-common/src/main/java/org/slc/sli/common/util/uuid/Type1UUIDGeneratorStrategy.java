package org.slc.sli.common.util.uuid;

import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * Generates Type 1 UUIDs.
 * @author smelody
 *
 */
@Component
public class Type1UUIDGeneratorStrategy implements UUIDGeneratorStrategy{


    @Override
    public UUID randomUUID() {

        // TODO, this is a type 4 uuid
        return UUID.randomUUID();
    }

}
