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


package org.slc.sli.api.security.schema;

import java.util.Set;

import org.slc.sli.domain.enums.Right;

/**
 * Provides security related data from data definition
 *
 * @author dkornishev
 *
 */
public interface SchemaDataProvider {
    public Set<Right> getRequiredReadLevels(String entityType, String fieldPath);

    public Set<Right> getRequiredWriteLevels(String entityType, String fieldPath);
    
    public Set<Right> getAllFieldRights(String entityType, boolean getReadRights);

    public String getDataSphere(String entityType);
    
    public String getReferencingEntity(String entityType, String fieldPath);
}
