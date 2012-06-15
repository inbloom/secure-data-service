package org.slc.sli.common.util.uuid;

import java.security.SecureRandom;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * Generates Type 1 (time-based) UUIDs, preceeded by a shard key 'YYYYRR-'.
 *
 * @author wscott
 */
@Component
@Qualifier("shardType1UUIDGeneratorStrategy")
public class ShardType1UUIDGeneratorStrategy implements UUIDGeneratorStrategy {
    
    private TimeBasedGenerator generator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
    SecureRandom r = new SecureRandom();

    /**
     * Generate a shardable type 1 random UUID .
     */
    @Override
    public String randomUUID() {
        StringBuilder builder = new StringBuilder();
        char c1 = (char) (r.nextInt(26) + 'a');
        char c2 = (char) (r.nextInt(26) + 'a');
        builder.append(new DateTime().getYear());
        builder.append(c1);
        builder.append(c2);
        builder.append("-");
        builder.append(generator.generate().toString());
        String uuid = builder.toString();
        return uuid;
    }
}
