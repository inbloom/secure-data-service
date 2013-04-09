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
package org.slc.sli.bulk.extract.treatment;

import org.slc.sli.domain.Entity;

/** Added Id field in the record.
 * @author tke
 *
 */
public class IdTreatment implements Treatment{

    private static final String ID_STRING = "id";

    @Override
    public Entity apply(Entity entity) {
        entity.getBody().put(ID_STRING, entity.getEntityId());
        return entity;
    }

}
