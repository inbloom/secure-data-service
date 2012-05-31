package org.slc.sli.common.util.uuid;

import java.util.Random;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

/**
 * Generates Type 1 (time-based) UUIDs, preceeded by a shard key 'YYYYRR-'.
 *
 * @author wscott
 */
@Component
public class ShardType1UUIDGeneratorStrategy implements UUIDGeneratorStrategy {

    private TimeBasedGenerator generator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
    Random r = new Random();

    /**
     * Generate a shardable type 1 random UUID .
     */
    @Override
    public String randomUUID() {
        char c1 = (char) (r.nextInt(26) + 'a');
        char c2 = (char) (r.nextInt(26) + 'a');
        return "" + new DateTime().getYear() + c1 + c2 + "-" + generator.generate();
    }

}
