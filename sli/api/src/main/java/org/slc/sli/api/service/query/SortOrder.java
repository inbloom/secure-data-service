package org.slc.sli.api.service.query;

import org.springframework.data.mongodb.core.query.Order;

/**
 * Enumeration indicating which direction sorting should occur in.
 * 
 * @author Ryan Farris <rfarris@wgen.net>
 */
public enum SortOrder {
    
    ascending, descending;
    
    protected Order toOrder() {
        if (this == ascending) {
            return Order.ASCENDING;
        } else if (this == descending) {
            return Order.DESCENDING;
        } else {
            return null;
        }
    }
}
