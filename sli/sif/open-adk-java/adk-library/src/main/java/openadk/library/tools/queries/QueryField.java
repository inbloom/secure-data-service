//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.queries;

import openadk.library.Condition;
import openadk.library.Query;

/**
 * This interface defines an optional interface that can be implemented by a class and mapped 
 * to an element to satisfy a SIF_Query.<p>
 * 
 * When mapped to a a SIF_Query element and passed in as part of the table to the 
 * {@link openadk.library.tools.queries.QueryFormatter#format(Query, Map)} 
 * method, this class will be invoked if the query contains a condition that maps to the field
 *
 */
public interface QueryField {
    /**
     * Returns a portion of an SQL query that should be used to satisfy this specific query condition
     * @param formatter The formatter that is formatting the query
     * @param query The SIF_Query that is being processed
     * @param cond The specific condition that this class should render
     * @return an sql query snippet appropriate for the formatter being used
     */
    String render( QueryFormatter formatter,
            Query query,
            Condition cond ) throws QueryFormatterException;
}
