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
package org.slc.sli.bulk.extract;

import java.io.FileNotFoundException;
import java.util.List;


/**
 * Process that is responsible for data extract from the primary data store.
 * @author tshewchuk
 *
 */
public interface Extractor {

    /**
     * Initializes the extractor
     * 
     * @param tenants The list of tenants whose data could be extracted
     * @param outputDirectory The directory that will contain a zip file containg the extract
     * @throws FileNotFoundException 
     */
    void init(List<String> tenants, String outputDirectory) throws FileNotFoundException;

    
    /**
     * Execute extraction for all tenants.
     */
    void execute();

    /**
     * Execute extraction for a particular tenant.
     *
     * @param tenant
     */
    void execute(String tenant);
    
    /**
     * Terminates processes at the end of the extract 
     */
    public void destroy();

}
