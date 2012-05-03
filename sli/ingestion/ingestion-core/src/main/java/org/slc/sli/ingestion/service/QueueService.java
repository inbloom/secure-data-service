package org.slc.sli.ingestion.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Provides a service bean for interacting with the master / worker queue
 *
 * @author smelody
 *
 */
@Component
public interface QueueService {

    public void postItem(Map<Object, Object> item);

    public Map<Object, Object> reserveItem();

    /**
     * Fetch the work items that the worker with the given ID has reserved.
     *
     * @param id
     * @return
     */
    public List<Map<Object, Object>> fetchItems(String workerId);

    public void purgeExpiredItems();

}
