package org.slc.sli.ingestion.queue;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;

/**
 *
 * Builds items that can be placed onto the queue for worker processing.
 *
 * @author smelody
 *
 */
@Component
public class ItemBuilder {

    @Autowired
    @Qualifier("shardType1UUIDGeneratorStrategy")
    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    public Map<String, Object> buildNewItem() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("_id", uuidGeneratorStrategy.randomUUID());
        map.put(ItemKeys.STATE, ItemValues.UNCLAIMED);

        return map;
    }
}
