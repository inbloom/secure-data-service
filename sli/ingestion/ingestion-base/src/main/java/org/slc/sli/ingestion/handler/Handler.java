/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.ingestion.handler;

import java.util.List;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Generic interface for handlers that requires handle methods with and without error support.
 *
 * @author dduran
 *
 * @param <T>
 *            type to handle
 * @param <O>
 *            type to return
 */
public interface Handler<T, O> {

    /**
     * Handle the provided item.
     *
     * @param item
     *            the object we want to handle
     * @return object defined in concrete implementation
     */
    O handle(T item);

    /**
     * Handle the provided type and utilize the provided ErrorReport to track errors.
     *
     * @param item
     *            the object we want to handle
     * @param errorReport
     *            an ErrorReport implementation in which errors can be tracked
     * @return object defined in concrete implementation
     */
    O handle(T item, ErrorReport errorReport);

    /**
     * Handle the provided items.
     *
     * @param items
     *            list of items to be handled.
     * @param errorReport
     *            an ErrorReport implementation in which errors can be tracked
     * @return list of objects defined in concrete implementation
     */
    List<O> handle(List<T> items, ErrorReport errorReport);

    O handle(T item, AbstractMessageReport report, AbstractReportStats reportStats);

    List<O> handle(List<T> items, AbstractMessageReport report, AbstractReportStats reportStats);

}
