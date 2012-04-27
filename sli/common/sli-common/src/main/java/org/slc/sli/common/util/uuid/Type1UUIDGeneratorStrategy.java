package org.slc.sli.common.util.uuid;

import java.util.UUID;

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

//    @PostConstruct
//    public void init() {
//
//        EthernetAddress nic = EthernetAddress.fromInterface();
//        generator = Generators.timeBasedGenerator(nic);
//    }

    /**
     * Generate a type 1 random UUID.
     */
    @Override
    public UUID randomUUID() {
        return generator.generate();
    }

}
