package org.slc.sli.common.util.uuid;

import java.util.UUID;

import javax.annotation.PostConstruct;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import org.springframework.stereotype.Component;

/**
 * Generates Type 1 UUIDs.
 *
 * @author smelody
 *
 */
@Component
public class Type1UUIDGeneratorStrategy implements UUIDGeneratorStrategy {

    private TimeBasedGenerator generator;

    @PostConstruct
    public void init() {

        EthernetAddress nic = EthernetAddress.fromInterface();
        generator = Generators.timeBasedGenerator(nic);
    }

    @Override
    public UUID randomUUID() {

        // TODO, this is a type 4 uuid
        return generator.generate();
        // return UUID.randomUUID();
    }

}
