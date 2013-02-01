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
package org.slc.sli.search.process;

import org.slc.sli.search.connector.SourceDatastoreConnector.Tenant;
import org.slc.sli.search.entity.IndexEntity.Action;

/**
 * Process that is responsible for data extract from the primary data store
 * @author agrebneva
 *
 */
public interface Extractor extends Process {

    /**
     * Execute extract
     * @param action - use the provided action to specify what kind of action the indexer should perform for the extract
     */
    void execute(Action action);

    /**
     * Execute for a particular tenant
     * @param tenant
     * @param action
     */
    void execute(Tenant tenant, Action action);

}
