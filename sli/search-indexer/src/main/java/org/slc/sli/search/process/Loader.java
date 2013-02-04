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

import java.io.File;

import org.slc.sli.search.entity.IndexEntity.Action;

/**
 * Loads records from a file for indexing
 *
 */
public interface Loader extends Process {
    
    /**
     * Process a given extract and trigger indexing. The filename must contain index name.
     * @param inFile - {index}.{collection}.{ord}.json
     */
    public abstract void processFile(File inFile);
    
    /**
     * Process a given extract and trigger indexing 
     * @param index - index name
     * @param action - action to execute for the extract. Defaults to index
     * @param inFile - file to process. No naming convention for this method needed.
     */
    public abstract void processFile(String index, Action action, File inFile);
    
}
