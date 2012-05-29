package org.slc.sli.common.util.uuid;

import org.springframework.stereotype.Component;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * Generates Type 1 (time-based) UUIDs.
 *
 * @author smelody
 *
 */
@Component
public class Type1UUIDGeneratorStrategy implements UUIDGeneratorStrategy {

    private TimeBasedGenerator generator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());

    /**
     * Generate a type 1 random UUID.
     */
    @Override
    public String randomUUID() {
        return generator.generate().toString();
    }

}
