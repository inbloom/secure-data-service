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

package org.slc.sli.dal.migration.strategy;

/**
 * An exception that occurs during migration of data from one version to another.
 * 
 * @author kmyers
 *
 */
public class MigrationException extends RuntimeException {
    
    private Exception e;
    
    /**
     * 
     * @param e
     */
    public MigrationException(Exception e) {
        this.e = e;
    }
    
    public Exception getUnderlyingException() {
        return this.e;
    }
}
