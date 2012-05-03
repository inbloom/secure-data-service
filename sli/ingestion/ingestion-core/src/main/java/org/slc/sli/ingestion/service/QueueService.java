package org.slc.sli.ingestion.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Provides a service bean for interacting with the master / worker queue
 * @author smelody
 *
 */
@Component
public interface QueueService {

    public void postItem( Map<String, Object> item );

    public Map<String, Object> reserveItem();

    /**
     * Fetch the work items that the worker with the given ID has reserved.
     * @param id
     * @return
     */
    public List<Map<String, Object>> fetchItems( String workerId );

    public void purgeExpiredItems();

    /** Returns the number of items in the queue, regardless of state */
    public long count();

    /** Removes all entries in the queue.
     * @return */
    public int clear();


}
