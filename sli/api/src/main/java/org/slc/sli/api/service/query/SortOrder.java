/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
