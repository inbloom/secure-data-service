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

package org.slc.sli.ingestion.model.da;

import com.mongodb.WriteConcern;

import org.springframework.data.mongodb.core.MongoAction;
import org.springframework.data.mongodb.core.WriteConcernResolver;

/**
 * Strategy of WriteConcernResolver for the BatchJob mongotemplate.
 *
 * @author dduran
 *
 */
public class BatchJobWriteConcernResolver implements WriteConcernResolver {

    @Override
    public WriteConcern resolve(MongoAction action) {

        Class<?> entityClass = action.getEntityClass();
        if (entityClass != null && "Error".equals(entityClass.getSimpleName())) {
            return WriteConcern.NORMAL;
        }

        return WriteConcern.SAFE;
    }

}
