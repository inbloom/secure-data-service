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

package org.slc.sli.api.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.NeutralQuery;

/**
 * A test utility class for having a mock basic service available for other uses.
 * 
 * @author kmyers
 *
 */
public class MockBasicService implements EntityService {

    List<Integer> numToReturn;
    int count = 0;

    public void setNumToReturn(List<Integer> numToReturn) {
        this.numToReturn = numToReturn;
    }

    @Override
    public long count(NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String create(EntityBody content) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean update(String id, EntityBody content) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean patch(String id, EntityBody content) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public EntityBody get(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityBody get(String id, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<EntityBody> list(NeutralQuery neutralQuery) {
        List<EntityBody> results = new ArrayList<EntityBody>();
        EntityBody body;
        for (int i = 0; i < numToReturn.get(count); i++) {
            body = new EntityBody();
            body.put("id", i);
            results.add(body);
        }
        count++;
        return results;
    }

    @Override
    public Iterable<String> listIds(NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<EntityBody> get(Iterable<String> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<EntityBody> get(Iterable<String> ids, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean exists(String id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public EntityDefinition getEntityDefinition() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityBody getCustom(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteCustom(String id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void createOrUpdateCustom(String id, EntityBody customEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public CalculatedData<String> getCalculatedValues(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CalculatedData<Map<String, Integer>> getAggregates(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean collectionExists(String collection) {
        // TODO Auto-generated method stub
        return true;
    }


}
