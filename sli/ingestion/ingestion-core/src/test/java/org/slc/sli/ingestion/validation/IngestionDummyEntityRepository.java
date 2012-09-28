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

package org.slc.sli.ingestion.validation;

import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.validation.DummyEntityRepository;

/**
 * Mock entity repository for testing purposes
 *
 * @author someone other than nbrown who copied my code and forgot to update the author annotation
 *
 */
@Component("validationRepo")
public class IngestionDummyEntityRepository extends DummyEntityRepository {

    @Override
    public boolean update(String collection, Entity entity) {
        addEntity(collection, entity.getEntityId(), entity);
        return true;
    }

    @Override
    public boolean exists(String collectionName, String id) {
        // TODO Auto-generated method stub
        return true;
    }

}
